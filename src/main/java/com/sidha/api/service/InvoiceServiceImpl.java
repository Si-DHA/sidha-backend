package com.sidha.api.service;

import com.sidha.api.model.Invoice;
import com.sidha.api.repository.InvoiceDb;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private InvoiceDb invoiceDb;

    @Override
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceDb.save(invoice);
    }

    @Override
    public Invoice createInvoice() {
        Invoice invoice = new Invoice();
        return this.saveInvoice(invoice);
    }
}
