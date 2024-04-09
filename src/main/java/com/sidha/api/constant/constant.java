package com.sidha.api.constant;

import java.util.HashMap;
import java.util.Map;

public class constant {
    
    public static final Map<Integer, String> statusOrderKlien = new HashMap<>() {
        {
            put(-1, "Ditolak");
            put(0, "Dalam review");
            put(1, "Diproses");
            put(2, "Menunggu DP");
            put(3, "Dalam Perjalanan");
            put(4, "Menunggu Pelunasan");
            put(5, "Selesai");
        }
    };
    
}