package com.fpt.edu.security.services;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Food;
import com.fpt.edu.models.Invoice;
import com.fpt.edu.repository.FoodRepository;
import com.fpt.edu.repository.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoiceService {
    @Autowired
    InvoiceRepository invoiceRepository;

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public Page<Invoice> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return invoiceRepository.findAll(pageable);
    }

    public Page<Invoice> findAllWithSort(String field, String direction, int pageNumber){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(field).ascending(): Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1,5, sort);
        return invoiceRepository.findAll(pageable);
    }

    public Page<Invoice> findByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return invoiceRepository.findByKeyword(keyword, pageable);
    }
}
