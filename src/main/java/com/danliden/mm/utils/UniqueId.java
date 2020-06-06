package com.danliden.mm.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UniqueId {

    private List<Integer> ids = new ArrayList<Integer>();

    public UniqueId(int range){
        for(int i = 0; i < range; i++){
            ids.add(i);
        }
    }

    public void giveBackID(int id){
        ids.add(id);
    }

    public int getId(){
        if(ids.size() == 0){
            return -1;
        }

        int id = ids.get(ids.size() - 1);
        ids.remove(ids.size() - 1);
        return id;
    }
}
