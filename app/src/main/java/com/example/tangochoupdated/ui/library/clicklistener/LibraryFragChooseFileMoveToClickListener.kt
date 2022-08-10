import android.content.Context
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.navigation.NavController
import com.example.tangochoupdated.R
import com.example.tangochoupdated.databinding.LibraryFragBinding
import com.example.tangochoupdated.databinding.LibraryFragSelectFileMoveToBaseBinding
import com.example.tangochoupdated.ui.library.LibraryViewModel

class LibraryFragChooseFileMoveToClickListener(val context:Context, val binding: LibraryFragSelectFileMoveToBaseBinding, val libVM: LibraryViewModel,
                                               val navCon:NavController): View.OnClickListener{

    val moveTo = binding.topBarChooseFileMoveToBinding


    override fun onClick(v: View?) {
        binding.apply {
            when(v){

                moveTo.imvCloseChooseFileMoveTo -> TODO()


            }
        }
    }

}