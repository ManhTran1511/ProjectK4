package com.fpt.edu.security.services;

import com.fpt.edu.models.Ban;
import com.fpt.edu.models.Blog;
import com.fpt.edu.repository.BanRepository;
import com.fpt.edu.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogService {
    @Autowired
    BlogRepository blogRepository;

    public List<Blog> findAll() {
        return blogRepository.findAll();
    }

    public Page<Blog> findPage(int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return blogRepository.findAll(pageable);
    }

    public Page<Blog> findAllWithSort(String field, String direction, int pageNumber){
        Sort sort = direction.equalsIgnoreCase(Sort.Direction.ASC.name())?
                Sort.by(field).ascending(): Sort.by(field).descending();
        Pageable pageable = PageRequest.of(pageNumber - 1,5, sort);
        return blogRepository.findAll(pageable);
    }

    public Page<Blog> findByKeyword(String keyword, int pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber -1, 5);
        return blogRepository.findByKeyword(keyword, pageable);
    }
}
