package com.sidha.api.service;

import com.sidha.api.DTO.request.KonfirmasiBuktiPembayaranDTO;
import com.sidha.api.model.Invoice;
import com.sidha.api.model.image.ImageData;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface InvoiceService {
    Invoice saveInvoice(Invoice invoice);

    Invoice findInvoiceById(UUID idInvoice);

    List<Invoice> findInvoiceByIdKlien(UUID idKlien);

    List<Invoice> findAllInvoice();

    void deleteInvoiceById(UUID idInvoice);

    Invoice createInvoice();

    Invoice uploadBuktiPembayaran(UUID idInvoice, boolean isPelunasan, MultipartFile imageFile) throws IOException;

    ImageData getImageBuktiPembayaran(UUID idInvoice, boolean isPelunasan);

    void deleteImageBuktiPembayaran(UUID idInvoice, boolean isPelunasan);

    Invoice konfirmasiBuktiPembayaran(KonfirmasiBuktiPembayaranDTO konfirmasiBuktiPembayaranDTO);
}
