import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hanto.Hook.repository.Repository
import kotlinx.coroutines.launch

class TagViewModel : ViewModel() {

    private val repository = Repository()

    private val _tags = MutableLiveData<List<String>>() // uniqueTag 값을 갖는 리스트
    val tags: LiveData<List<String>>
        get() = _tags

    init {
        getUniqueTag()
    }

    private fun getUniqueTag() {
        viewModelScope.launch {
            try {
                val allTags = repository.getAllTag() // 모든 태그 데이터 가져오기
                val uniqueTags = allTags.mapNotNull { it.uniqueTag } // 각 태그에서 uniqueTag 값 추출
                _tags.value = uniqueTags // uniqueTag 값만을 갖는 리스트를 LiveData에 할당
            } catch (e: Exception) {
                // 에러 처리
            }
        }
    }

}

