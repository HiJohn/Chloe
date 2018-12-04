package joe.chloe.exoplayerfilter;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

public class PlayerViewModel extends AndroidViewModel {


    private MutableLiveData<Integer> seekBarValue = new MutableLiveData<>();

    public PlayerViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<Integer> getSeekBarValue() {
        return seekBarValue;
    }

    public void setSeekBarValue(int value) {
        this.seekBarValue.setValue(value);
    }
}
