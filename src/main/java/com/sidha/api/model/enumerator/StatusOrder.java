package com.sidha.api.model.enumerator;

public enum StatusOrder {
  DIBUAT,
  DIPROSES_KARYAWAN,
  DITOLAK,
  MENUNGGU_DP,
  DIANGKUT,
  DIANTARKAN,
  SAMPAI,
  MENUNGGU_PELUNASAN,
  SELESAI,;

  public String getName() {
    return name();
  }
}
