package com.bittle.colorpicker.realm;

import android.content.Context;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Scribbled by oscartorres on 5/20/18.
 */

// helper for realm db
public class DBRealm {

    private static DBRealm instance = null;

    public static DBRealm getInstance(Context context) {
        if (instance == null)
            instance = new DBRealm(context);
        return instance;
    }

    private Realm realm;

    private DBRealm(Context context) {
        // initialize realm
        Realm.init(context);
        RealmConfiguration config = new RealmConfiguration.Builder().name("colorpicker.realm").build();
        Realm.setDefaultConfiguration(config);
        start();
    }

    // insert color into db
    public void insert(String hex) {
        try {
            realm.beginTransaction();
            ColorModel c = realm.where(ColorModel.class).
                    equalTo("hex", hex).findFirst();
            if (c == null) {
                realm.insert(new ColorModel(hex));
            } else {
                realm.insertOrUpdate(c.updateTime());
            }
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public RealmResults<ColorModel> findAll() {
        return realm.where(ColorModel.class).sort("timestamp", Sort.DESCENDING).findAll();
    }

    // clear db
    public void clearAll() {
        try {
            realm.beginTransaction();
            realm.where(ColorModel.class).findAll().deleteAllFromRealm();
            realm.commitTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        this.realm = Realm.getDefaultInstance(); // opens "colorpicker.realm"
    }
    public void close(){
        realm.close();
    }
}
