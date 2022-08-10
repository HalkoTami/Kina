import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragOpenFlashCardCoverBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragOpenFolderBaseBinding
import com.example.tangochoupdated.databinding.LibraryFragTopBarFileBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibraryFragFileClickListener(val context:Context,
                                   val binding: LibraryFragTopBarFileBinding,
                                   val libVM: LibraryViewModel,
                                   val navCon:NavController): View.OnClickListener{

    val file = binding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                binding.imvGoBack -> navCon.popBackStack()
                file.txvGGrandParentFileTitle -> TODO()
                file.txvGrandParentFileTitle -> TODO()




            }
        }
    }

}