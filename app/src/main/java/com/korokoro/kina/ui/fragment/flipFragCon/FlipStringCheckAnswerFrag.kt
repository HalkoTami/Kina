package com.korokoro.kina.ui.fragment.flipFragCon

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.korokoro.kina.R
import com.korokoro.kina.databinding.AnkiFlipFragCheckAnswerStringFragBinding
import com.korokoro.kina.db.dataclass.ActivityData
import com.korokoro.kina.db.dataclass.Card
import com.korokoro.kina.db.enumclass.ActivityStatus
import com.korokoro.kina.ui.customClasses.FlipFragments
import com.korokoro.kina.ui.viewmodel.AnkiFlipBaseViewModel
import com.korokoro.kina.ui.viewmodel.FlipTypeAndCheckViewModel
import com.korokoro.kina.ui.viewmodel.AnkiSettingPopUpViewModel


class FlipStringCheckAnswerFrag  : Fragment() {

    private var _binding: AnkiFlipFragCheckAnswerStringFragBinding? = null
    private val args: FlipStringCheckAnswerFragArgs by navArgs()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    private val flipTypeAndCheckViewModel: FlipTypeAndCheckViewModel by activityViewModels()
    private val ankiFlipBaseViewModel: AnkiFlipBaseViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fun getCharacter(answerRight:Boolean):Drawable{
            return AppCompatResources.getDrawable(requireActivity(),
                if(answerRight) R.drawable.tosaka_light_ball else R.drawable.character1_falling)!!
        }
        fun getResultIcon(answerRight:Boolean):Drawable{
            return AppCompatResources.getDrawable(requireActivity(),
                if(answerRight) R.drawable.icon_correct else R.drawable.icon_missed)!!
        }
        fun getSpeakBubbleContent(answerRight:Boolean):String{
            return if(answerRight) "正解！" else "残念"
        }
        fun setResultContent(answerCorrect:Boolean){
            binding.imvResultInRow.setImageDrawable(getResultIcon(answerCorrect))
            binding.imvCharacterTypedAnswerReaction.setImageDrawable(getCharacter(answerCorrect))
            binding.txvSpeakBubbleContent.text = getSpeakBubbleContent(answerCorrect)
            binding.imvResultIcon.setImageDrawable(getResultIcon(answerCorrect))
        }
        fun setResultContentNoData(boolean: Boolean){
            binding.linLayAnswerResult.alpha = if(boolean) 0.5f else 1f
        }
        fun filterChallengedTimes(answerIsBack:Boolean, results:List<ActivityData>):List<ActivityData>{
            val challengeStatus = if(answerIsBack) arrayOf(ActivityStatus.RIGHT_BACK_CONTENT_TYPED,ActivityStatus.WRONG_BACK_CONTENT_TYPED) else
                arrayOf(ActivityStatus.WRONG_FRONT_CONTENT_TYPED,ActivityStatus.RIGHT_FRONT_CONTENT_TYPED)
            return  results.filter {challengeStatus.contains(it.activityStatus)}
        }
        fun getResultsInRow(results:List<ActivityData>):Int{
            val filtered = filterChallengedTimes(args.answerIsBack,results)
            var rowCount = 1
            while (rowCount<filtered.size-1&& filtered[filtered.size-1-rowCount].activityStatus == filtered.last().activityStatus){
                rowCount++
            }
            return rowCount

        }
        fun getAnswerCorrect(activityStatus:ActivityStatus):Boolean{
            return when(activityStatus){
                ActivityStatus.WRONG_BACK_CONTENT_TYPED,
                ActivityStatus.WRONG_FRONT_CONTENT_TYPED,-> false
                ActivityStatus.RIGHT_BACK_CONTENT_TYPED,
                ActivityStatus.RIGHT_FRONT_CONTENT_TYPED -> true
                else -> false
            }
        }

        fun getChallengedTimes(answerIsBack:Boolean,results:List<ActivityData>):Int{
            return filterChallengedTimes(answerIsBack,results).size
        }
        fun getAnswerCorrectTimes(answerIsBack:Boolean,results:List<ActivityData>):Int{
            return results.filter { it.activityStatus == if(answerIsBack)ActivityStatus.RIGHT_BACK_CONTENT_TYPED else ActivityStatus.RIGHT_FRONT_CONTENT_TYPED }.size
        }
        _binding =  AnkiFlipFragCheckAnswerStringFragBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val cardFromDBObserver = Observer<Card>{
            ankiFlipBaseViewModel.setParentCard(it)
            binding.txvTitle.text = if(args.answerIsBack)it.stringData?.backTitle ?:"裏" else it.stringData?.frontTitle ?:"表"
            binding.txvCorrectAnswer.text = if(args.answerIsBack)it.stringData?.backText else it.stringData?.frontText
        }
        val typedAnswersObserver = Observer<MutableMap<Int,String>> {
            binding.txvYourAnswer.text = it[args.cardId]
        }
        val activityDataObserver = Observer<List<ActivityData>> {
            setResultContentNoData(it.isNullOrEmpty())
            if(it.isNullOrEmpty().not()){
                val results = it.filter { arrayOf(
                    ActivityStatus.WRONG_BACK_CONTENT_TYPED,
                    ActivityStatus.RIGHT_BACK_CONTENT_TYPED,
                    ActivityStatus.WRONG_FRONT_CONTENT_TYPED,
                    ActivityStatus.RIGHT_FRONT_CONTENT_TYPED).contains(it.activityStatus)  }
                setResultContent(getAnswerCorrect(results.last().activityStatus))
                binding.txvResultInRow.text  = getResultsInRow(it).toString() +"連続"+(if(getAnswerCorrect(results.last().activityStatus)) "正解" else "不正解") +"中"
                val correct = getAnswerCorrectTimes(args.answerIsBack,results)
                val challenged = getChallengedTimes(args.answerIsBack,results)
                binding.progressbarAnswerCorrect.progress =
                    (correct/challenged.toDouble()*100).toInt()
                binding.txvProgressAnswerCorrect.text = "正答率:${(correct/challenged.toDouble()*100).toInt()}%"
            }
        }
        ankiFlipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner,cardFromDBObserver)
        ankiFlipBaseViewModel.onChildFragmentsStart(
            FlipFragments.CheckAnswerString,
            ankiSettingPopUpViewModel.returnReverseCardSide(),
            ankiSettingPopUpViewModel.returnAutoFlip().active)
        flipTypeAndCheckViewModel.typedAnswers.observe(viewLifecycleOwner,typedAnswersObserver)
        flipTypeAndCheckViewModel.getActivityData(args.cardId).observe(viewLifecycleOwner,activityDataObserver)



        return root
    }
    fun dpTopx(dp: Int, context: Context): Float {
        val metrics = context.getResources().getDisplayMetrics()
        return dp * metrics.density
    }

    fun pxToDp(px: Int, context: Context): Float {
        val metrics = context.resources.displayMetrics
        return px / metrics.density
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}