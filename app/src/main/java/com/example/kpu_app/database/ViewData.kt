import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.room1.database.Pemilih
import com.example.room1.database.PemilihDao
import kotlinx.coroutines.launch

class ViewData(private val pemilihDao: PemilihDao) : ViewModel() {

    val allPemilih: LiveData<List<Pemilih>> = pemilihDao.allPemilih

    fun insertPemilih(pemilih: Pemilih) {
        viewModelScope.launch {
            pemilihDao.insert(pemilih)
        }
    }

}
