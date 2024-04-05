package com.sidha.api.service;

import com.sidha.api.model.Invoice;

public interface InvoiceService {
    Invoice saveInvoice(Invoice invoice);

    Invoice createInvoice();
}
