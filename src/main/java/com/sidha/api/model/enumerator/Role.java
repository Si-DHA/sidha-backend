package com.sidha.api.model.enumerator;

public enum Role {
  KLIEN,
  KARYAWAN,
  ADMIN,
  SOPIR;

  public String getName() {
      return name();
  }
}
