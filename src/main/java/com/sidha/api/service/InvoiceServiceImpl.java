package com.sidha.api.service;

import com.sidha.api.DTO.request.UploadBuktiPembayaranDTO;
import com.sidha.api.model.Invoice;
import com.sidha.api.model.image.ImageData;
import com.sidha.api.model.image.InvoiceImage;
import com.sidha.api.model.image.ProfileImage;
import com.sidha.api.repository.ImageDataRepository;
import com.sidha.api.repository.InvoiceDb;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private InvoiceDb invoiceDb;

    private StorageService storageService;

    private ImageDataRepository imageDataRepository;

    private ModelMapper modelMapper;

    @Override
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceDb.save(invoice);
    }

    @Override
    public Invoice createInvoice() {
        Invoice invoice = new Invoice();
        return this.saveInvoice(invoice);
    }

    @Override
    public Invoice uploadBuktiPembayaran(UploadBuktiPembayaranDTO uploadBuktiPembayaranDTO) throws IOException {
        UUID idInvoice = uploadBuktiPembayaranDTO.getIdInvoice();
        Invoice invoice = invoiceDb.findById(idInvoice).orElse(null);

        if (invoice == null) {
            throw new NoSuchElementException("Id invoice tidak valid");
        }

        MultipartFile imageFile = uploadBuktiPembayaranDTO.getImageFile();
        ImageData imageData = storageService.uploadImageAndSaveToDB(
                imageFile,
                idInvoice + "_" + storageService.replaceWhitespaceWithUnderscore(imageFile.getOriginalFilename()));

        InvoiceImage invoiceImage = modelMapper.map(imageData, InvoiceImage.class);

        if (!uploadBuktiPembayaranDTO.isPelunasan()) {
            invoice.setBuktiDp(invoiceImage);
        } else {
            invoice.setBuktiPelunasan(invoiceImage);
        }
        this.saveInvoice(invoice);

        invoiceImage.setInvoice(invoice);
        imageDataRepository.save(invoiceImage);

        return invoice;
    }
}
