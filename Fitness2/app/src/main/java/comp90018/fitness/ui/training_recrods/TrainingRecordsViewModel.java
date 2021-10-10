package comp90018.fitness.ui.training_recrods;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TrainingRecordsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public TrainingRecordsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is training records fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}