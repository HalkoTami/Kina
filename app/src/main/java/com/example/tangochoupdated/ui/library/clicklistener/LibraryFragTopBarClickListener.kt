import android.view.View
import androidx.navigation.NavController
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibraryFragTopBarClickListener(val binding: LibraryFragBinding,val libVM: LibraryViewModel,
val navCon:NavController): View.OnClickListener{
    val home = binding.topBarHomeBinding
    val file = binding.topBarFileBinding
    val inBox = binding.topBarInboxBinding
    val multi = binding.topBarMultiselectBinding
    val moveTo = binding.topBarChooseFileMoveToBinding
    fun changeMenuVisibility(){
            multi.frameLayMultiModeMenu.apply{
                visibility =  if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                home.frameLayInBox-> libVM.onClickInBox()
                home.imvBookMark -> TODO()

                file.imvGoBack -> navCon.popBackStack()
                file.txvGGrandParentFileTitle -> TODO()
                file.txvGrandParentFileTitle -> TODO()

                inBox.imvMoveToFlashCard -> TODO()
                inBox.imvClose -> navCon.popBackStack()

                multi.imvClose -> libVM.setMultipleSelectMode(false)
                multi.imvSelectAll -> TODO()
                multi.imvChangeMenuVisibility -> changeMenuVisibility()

                multi.multiSelectMenuBinding.imvMoveSelectedItems -> TODO()
                multi.multiSelectMenuBinding.imvDeleteSelectedItems -> TODO()
                multi.multiSelectMenuBinding.imvSetFlagToSelectedItems -> TODO()

                moveTo.imvCloseChooseFileMoveTo -> TODO()


            }
        }
    }

}