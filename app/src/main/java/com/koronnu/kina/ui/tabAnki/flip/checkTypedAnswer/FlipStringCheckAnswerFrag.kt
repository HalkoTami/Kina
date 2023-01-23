package com.koronnu.kina.ui.tabAnki.flip.checkTypedAnswer

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.koronnu.kina.R
import com.koronnu.kina.data.model.enumClasses.FlipFragments
import com.koronnu.kina.databinding.FragmentFlipStringCheckAnswerBinding
import com.koronnu.kina.data.source.local.entity.ActivityData
import com.koronnu.kina.data.source.local.entity.Card
import com.koronnu.kina.data.source.local.entity.enumclass.ActivityStatus
import com.koronnu.kina.ui.tabAnki.flip.AnkiFlipBaseViewModel
import com.koronnu.kina.ui.tabAnki.AnkiSettingPopUpViewModel
import com.koronnu.kina.ui.tabAnki.flip.FlipTypeAndCheckViewModel


class FlipStringCheckAnswerFrag  : Fragment() {

    private var _binding: FragmentFlipStringCheckAnswerBinding? = null
    private val args: com.koronnu.kina.ui.tabAnki.flip.checkTypedAnswer.FlipStringCheckAnswerFragArgs by navArgs()
    private val ankiSettingPopUpViewModel: AnkiSettingPopUpViewModel by activityViewModels()
    private val flipTypeAndCheckViewModel: FlipTypeAndCheckViewModel by viewModels { FlipTypeAndCheckViewModel.Factory }
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
            return resources.getString(if(answerRight) R.string.flipCheckAnswerStringFragBin_spbContent_correct else R.string.flipCheckAnswerStringFragBin_spbContent_wrong)
        }
        fun setResultContent(answerCorrect:Boolean){
            binding.imvResultInRow.setImageDrawable(getResultIcon(answerCorrect))
            binding.imvCharacterTypedAnswerReaction.setImageDrawable(getCharacter(answerCorrect))
            binding.txvSpeakBubbleContent.text = getSpeakBubbleContent(answerCorrect)
            binding.imvResultIcon.setImageDrawable(getResultIcon(answerCorrect))
        }
        fun setResultContentNoData(boolean: Boolean){
            binding.llAnswerResult.alpha = if(boolean) 0.5f else 1f
        }
        fun filterChallengedTimes(answerIsBack:Boolean, results:List<ActivityData>):List<ActivityData>{
            val challengeStatus = if(answerIsBack) arrayOf(ActivityStatus.RIGHT_BACK_CONTENT_TYPED,
                ActivityStatus.WRONG_BACK_CONTENT_TYPED) else
                arrayOf(ActivityStatus.WRONG_FRONT_CONTENT_TYPED, ActivityStatus.RIGHT_FRONT_CONTENT_TYPED)
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
        fun getAnswerCorrect(activityStatus: ActivityStatus):Boolean{
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
            return results.filter { it.activityStatus == if(answerIsBack) ActivityStatus.RIGHT_BACK_CONTENT_TYPED else ActivityStatus.RIGHT_FRONT_CONTENT_TYPED }.size
        }
        fun addCL(){
            binding.imvFlipCorrectAnswerSide.setOnClickListener {
                val data = ankiFlipBaseViewModel.getParentCard.stringData
                it.isSelected = it.isSelected.not()
                if(it.isSelected){
                    it.alpha = 1f
                    binding.txvCorrectAnswerCardTitle.text = if(args.answerIsBack)data?.frontTitle ?:resources.getString(R.string.edtFrontTitle_default) else data?.backTitle ?:resources.getString(R.string.edtBackTitle_default)
                    binding.txvCorrectAnswer.text = if(args.answerIsBack)data?.frontText else data?.backText
                } else {
                    it.alpha = 0.5f
                    binding.txvCorrectAnswerCardTitle.text = (if(args.answerIsBack)data?.backTitle  else data?.frontTitle )?:resources.getString(R.string.answer)
                    binding.txvCorrectAnswer.text = if(args.answerIsBack)data?.backText else data?.frontText
                }

            }
        }
        _binding =  FragmentFlipStringCheckAnswerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val cardFromDBObserver = Observer<Card>{
            ankiFlipBaseViewModel.setParentCard(it)
            binding.txvCorrectAnswerCardTitle.text = (if(args.answerIsBack)it.stringData?.backTitle  else it.stringData?.frontTitle) ?:resources.getString(R.string.answer)
            binding.txvCorrectAnswer.text = if(args.answerIsBack)it.stringData?.backText else it.stringData?.frontText
        }
        val typedAnswersObserver = Observer<MutableMap<Int,String>> {
            binding.txvYourAnswer.text = it[args.cardId].toString()
        }
        val activityDataObserver = Observer<List<ActivityData>> { activityDataList ->
            setResultContentNoData(activityDataList.isNullOrEmpty())
            if(activityDataList.isNullOrEmpty().not()){
                val results = activityDataList.filter { arrayOf(
                    ActivityStatus.WRONG_BACK_CONTENT_TYPED,
                    ActivityStatus.RIGHT_BACK_CONTENT_TYPED,
                    ActivityStatus.WRONG_FRONT_CONTENT_TYPED,
                    ActivityStatus.RIGHT_FRONT_CONTENT_TYPED).contains(it.activityStatus)  }
                if(results.isEmpty()) return@Observer
                setResultContent(getAnswerCorrect(results.last().activityStatus))
                binding.txvResultInRow.text  = resources.getString(R.string.txvResultInRow,getResultsInRow(activityDataList),
                    if(getAnswerCorrect(results.last().activityStatus)) resources.getString(R.string.answer_correct) else resources.getString(R.string.answer_incorrect))
                val correct = getAnswerCorrectTimes(args.answerIsBack,results)
                val challenged = getChallengedTimes(args.answerIsBack,results)
                binding.pgbAnswerCorrect.progress =
                    (correct/challenged.toDouble()*100).toInt()
                binding.txvProgressAnswerCorrect.text = resources.getString(R.string.answerCorrectInRowPercentage,(correct/challenged.toDouble()*100).toInt())
            }
        }
        addCL()
        ankiFlipBaseViewModel.getCardFromDB(args.cardId).observe(viewLifecycleOwner,cardFromDBObserver)
        ankiFlipBaseViewModel.onChildFragmentsStart(
            FlipFragments.CheckAnswerString,
            ankiSettingPopUpViewModel.getAutoFlip.active)
        flipTypeAndCheckViewModel.typedAnswers.observe(viewLifecycleOwner,typedAnswersObserver)
        flipTypeAndCheckViewModel.getActivityData(args.cardId).observe(viewLifecycleOwner,activityDataObserver)
        flipTypeAndCheckViewModel.setKeyBoardVisible(false)


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}