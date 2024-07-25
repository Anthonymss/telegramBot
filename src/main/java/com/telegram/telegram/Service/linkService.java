package com.telegram.telegram.Service;

import com.telegram.telegram.entitys.link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.telegram.telegram.repository.linkRepository;
import java.util.List;

@Service
public class linkService {
    @Autowired
    private linkRepository linkRepository;
    public String Guardar(link link){
        if(link!=null){
            linkRepository.save(link);
            return "Se guardo correctamente";
        }else {
        return "Objeto nulo invalido";
        }
    }
    public List<link> getLinks(){
        return this.linkRepository.findAll();
    }
}
