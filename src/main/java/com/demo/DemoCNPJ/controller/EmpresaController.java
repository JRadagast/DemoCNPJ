package com.demo.DemoCNPJ.controller;

import com.demo.DemoCNPJ.bean.Empresa;
import com.demo.DemoCNPJ.service.EmpresaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class EmpresaController {

    @Autowired
    private EmpresaService service;

    @CrossOrigin
    @GetMapping("/democnpj/empresa/busca-cnpj")
    public ResponseEntity<Empresa> getEmpresaByCnpj(@RequestParam(name="cnpj") String cnpj ) {
        String uri = "https://publica.cnpj.ws/cnpj/";
        RestTemplate restTemplate = new RestTemplate();

        String result = restTemplate.getForObject(uri + cnpj, String.class);

        ResponseEntity<Empresa> r = service.getEmpresaFromJSON( result );

        return r;
    }

    @CrossOrigin
    @PostMapping("/democnpj/empresa/salvar")
    public ResponseEntity<Empresa> save( @RequestBody Empresa empresa ) {

        ResponseEntity<Empresa> r = service.save( empresa );

        return r;
    }

}
