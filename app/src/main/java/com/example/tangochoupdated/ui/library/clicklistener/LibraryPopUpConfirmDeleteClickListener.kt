import android.view.View
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibraryPopUpConfirmDeleteClickListener(val binding: LibraryFragBinding, val libVM: LibraryViewModel): View.OnClickListener{
    val onlyP = binding.confirmDeletePopUpBinding
    val deleteAllC = binding.confirmDeleteChildrenPopUpBinding
    override fun onClick(v: View?) {
        binding.apply {
            when(v){
                onlyP.btnCloseConfirmDeleteOnlyParentPopup -> TODO()
                onlyP.btnCommitDeleteOnlyParent -> TODO()
                onlyP.btnDenyDeleteOnlyParent -> TODO()
                deleteAllC.btnCloseConfirmDeleteOnlyParentPopup -> TODO()
                deleteAllC.btnCommitDeleteAllChildren -> TODO()
                deleteAllC.btnDenyDeleteAllChildren -> TODO()


            }
        }
    }
}