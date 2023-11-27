package com.demo.DemoCNPJ.service;

import com.demo.DemoCNPJ.bean.Empresa;
import com.demo.DemoCNPJ.repository.EmpresaRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.SQLOutput;

@Service
public class EmpresaService {

    @Autowired
    private EmpresaRepository repository;

    /**
     * Deserializa o JSON e busca apenas os campos que necessitamos. Foi feito desta forma para que possamos ter um retorno mais customizado para os campos desejados.
     * @param json
     * @return
     */
    public ResponseEntity<Empresa> getEmpresaFromJSON(String json){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            /**
             * Le o json e retorna o json node.
             */
            JsonNode node = objectMapper.readTree(json);
            JsonNode estabelecimento = node.get("estabelecimento");
            Empresa e = new Empresa();

            e.setCnpj(node.get("cnpj_raiz").asText());
            /* Verifica se cada campo buscado existe primeiro, caso exista, pega-se o valor. Se não, apenas ignora.*/
            if (node.has("razao_social")) {
                e.setRazaoSocial(node.get("razao_social").asText());
            }
            /*
            if (estabelecimento.has("tipo_logradouro") && estabelecimento.has("logradouro") && estabelecimento.has("numero") && estabelecimento.has("bairro")){
                e.setEndereco(estabelecimento.get("tipo_logradouro").asText() + " " + estabelecimento.get("logradouro").asText() + " " + estabelecimento.get("numero").asText() + " - " + estabelecimento.get("bairro").asText());
            } else if ( estabelecimento.has("tipo_logradouro") && estabelecimento.has("logradouro") ) {
                e.setEndereco(estabelecimento.get("tipo_logradouro").asText() + " " + estabelecimento.get("logradouro").asText());
            }
            if ( estabelecimento.has("telefone1") ) {
                e.setTelefone( estabelecimento.get("telefone1").asText() );
            }
             */
            if ( estabelecimento.has("situacao_cadastral")) {
                e.setSituacaoCadastral(estabelecimento.get("situacao_cadastral").asText());
            }
            if ( estabelecimento.has("data_situacao_cadastral") ) {
                e.setDataCadastro(Date.valueOf(estabelecimento.get("data_situacao_cadastral").asText()));
            }
            JsonNode cidade = estabelecimento.get("cidade");
            if (cidade != null) {
                e.setCidade(cidade.get("nome").asText());
            }

            ResponseEntity<Empresa> response = new ResponseEntity<Empresa>(e, HttpStatus.OK);

            return response;
        } catch (Exception ex){

            ex.printStackTrace();
            ResponseEntity<Empresa> response = new ResponseEntity<Empresa>(HttpStatus.INTERNAL_SERVER_ERROR);

            return response;
        }
    }

    public ResponseEntity<Empresa> save(Empresa empresa){
        try {
            Empresa e = repository.getByCnpj( empresa.getCnpj() );
            if (e != null){
                e = repository.update(empresa);
            } else {
                e = repository.save(empresa);
            }

            ResponseEntity<Empresa> response = new ResponseEntity<Empresa>(e, HttpStatus.OK);

            return response;
        } catch (SQLException ex){

            ResponseEntity<Empresa> response = new ResponseEntity<Empresa>(HttpStatus.INTERNAL_SERVER_ERROR);

            return response;
        }
    }

}
