package it.univaq.disim.mwt.covid19italy.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import it.univaq.disim.mwt.covid19italy.Data.Provincia;

public class ProvinceViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Provincia>> province = new MutableLiveData<>();

    public void setProvince(ArrayList<Provincia> province) {
        this.province.setValue(province);
    }

    public MutableLiveData<ArrayList<Provincia>> getProvince() {
        return this.province;
    }

}
