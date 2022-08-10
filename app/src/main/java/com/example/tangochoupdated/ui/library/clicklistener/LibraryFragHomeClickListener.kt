import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragHomeBaseBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibraryFragHomeClickListener(val context:Context, val binding: LibraryFragHomeBaseBinding, val libVM: LibraryViewModel,
                                   val navCon:NavController): View.OnClickListener{
    val home = binding.topBarHomeBinding
    val multi = binding.topBarMultiselectBinding
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