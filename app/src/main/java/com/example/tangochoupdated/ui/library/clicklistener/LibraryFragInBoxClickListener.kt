import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragInboxBaseBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibraryFragInBoxClickListener(val context:Context, val binding: LibraryFragInboxBaseBinding, val libVM: LibraryViewModel,
                                    val navCon:NavController): View.OnClickListener{
    val inBox = binding.topBarInboxBinding
    val multi = binding.topBarMultiselectBinding
    fun changeMenuVisibility(){
            multi.frameLayMultiModeMenu.apply{
                visibility =  if(this.visibility == View.VISIBLE) View.GONE else View.VISIBLE
            }
        }

    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                inBox.imvMoveToFlashCard -> {
                    binding.topBarMultiselectBinding.multiSelectMenuBinding.apply {
                        imvMoveSelectedItems.setImageDrawable(AppCompatResources.getDrawable(context,R.drawable.icon_move_to_flashcard_cover))
                        arrayOf(imvDeleteSelectedItems,imvSetFlagToSelectedItems).onEach {
                            it.visibility = View.GONE
                        }
                    }
                    libVM.onClickMoveInBoxCardToFlashCard()
                }
                inBox.imvCloseInbox -> {
                    libVM.onClickCloseInBox()
                    navCon.popBackStack()
                }

                multi.imvCloseMultiMode -> libVM.setMultipleSelectMode(false)
                multi.imvSelectAll -> TODO()
                multi.imvChangeMenuVisibility -> changeMenuVisibility()

                multi.multiSelectMenuBinding.imvMoveSelectedItems -> libVM.chooseFileMoveTo()
                multi.multiSelectMenuBinding.imvDeleteSelectedItems -> TODO()
                multi.multiSelectMenuBinding.imvSetFlagToSelectedItems -> TODO()



            }
        }
    }

}