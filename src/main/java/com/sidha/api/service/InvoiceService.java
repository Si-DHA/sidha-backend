package com.sidha.api.service;

import com.sidha.api.DTO.request.UploadBuktiPembayaranDTO;
import com.sidha.api.model.Invoice;

import java.io.IOException;

public interface InvoiceService {
    Invoice saveInvoice(Invoice invoice);

    Invoice createInvoice();

    Invoice uploadBuktiPembayaran(UploadBuktiPembayaranDTO uploadBuktiPembayaranDTO) throws IOException;
}
