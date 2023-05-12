package com.fpt.edu.security.services;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Gallery;
import com.fpt.edu.repository.BanRepository;
import com.fpt.edu.repository.GalleryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GalleryService {
    @Autowired
    GalleryRepository galleryRepository;

    public List<Gallery> findAll() {
        return galleryRepository.findAll();
    }

    public Page<Gallery> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return galleryRepository.findAll(pageable);
    }

    public Page<Gallery> findAllWithSort(String field, String direction, int pageNumber){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(field).ascending(): Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1,5, sort);
        return galleryRepository.findAll(pageable);
    }

    public Page<Gallery> findByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return galleryRepository.findByKeyword(keyword, pageable);
    }
}
