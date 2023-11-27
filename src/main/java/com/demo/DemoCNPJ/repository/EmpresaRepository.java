package com.demo.DemoCNPJ.repository;

import com.demo.DemoCNPJ.SpringJdbcConfig;
import com.demo.DemoCNPJ.bean.Empresa;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class EmpresaRepository {

    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private Environment env;

    /**
     * As SQLs são criadas como static, desta forma não precisa carregar novamente as SQLs para cada instancia criada de EmpresaRepository.
     */
    private static String QUERY_SAVE = "INSERT INTO Empresa (razao_social, cidade, situacao_cadastral, data_cadastro, endereco, telefone, cnpj) VALUES " +
            "( :RAZAOSOCIAL, :CIDADE, :SITUACAOCADASTRAL, :DATACADASTRO, :ENDERECO, :TELEFONE, :CNPJ )";
    
    private static String QUERY_UPDATE = "UPDATE Empresa SET " +
            " razao_social = :RAZAOSOCIAL, cidade = :CIDADE, situacao_cadastral = :SITUACAOCADASTRAL, data_cadastro = :DATACADASTRO, endereco = :ENDERECO, telefone = :TELEFONE where cnpj = :CNPJ ";
    
    private static String QUERY_FIND_BY_ID = "SELECT * FROM Empresa WHERE cnpj = :CNPJ";
    
    /**
     * Insert empresa no banco de dados
     * 
     * @param empresa
     * @return 
     */
    public Empresa save(Empresa empresa) {
        setDataSource();
        MapSqlParameterSource params = getParamsInsert( empresa );

        this.jdbcTemplate.update(QUERY_SAVE, params);
        Empresa e = null;

        /* Verifica se a empresa foi adiciona com sucesso. */
        try {
            e = getByCnpj( empresa.getCnpj() );
        } catch (SQLException ex){
            ex.printStackTrace();
        }

        return e;
    }
    
    /**
     * Atualiza o registro da empresa
     * @param empresa
     * @return
     * @throws SQLException 
     */
    public Empresa update( Empresa empresa ) throws SQLException {
        setDataSource();
        MapSqlParameterSource params = getParamsInsert( empresa );

        this.jdbcTemplate.update(QUERY_UPDATE, params);

        return empresa;
    }

    /* Define os parametros usados nas SQLs da Empresa. */
    public MapSqlParameterSource getParamsInsert( Empresa empresa ){
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("RAZAOSOCIAL", empresa.getRazaoSocial() );
        params.addValue("CIDADE", empresa.getCidade() );
        params.addValue("SITUACAOCADASTRAL", empresa.getSituacaoCadastral() );
        params.addValue("DATACADASTRO", empresa.getDataCadastro() );
        params.addValue("ENDERECO", empresa.getEndereco() );
        params.addValue("TELEFONE", empresa.getTelefone() );
        params.addValue("CNPJ", empresa.getCnpj() );

        return params;
    }

    /***
     * Busca o usuário por CNPJ
     * 
     * @param cnpj
     * @return
     * @throws SQLException 
     */
    public Empresa getByCnpj(String cnpj ) throws SQLException {
        setDataSource();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("CNPJ", cnpj);

        List<Empresa> e = this.jdbcTemplate.query(QUERY_FIND_BY_ID, params, rowMapper() );

        return e != null && e.size() > 0 ? e.get(0) : null;
    }

    /**
     * Mapeia os dados da empresa do banco de dados para o objeto.
     * 
     * @return
     * @throws SQLException 
     */
    public RowMapper<Empresa> rowMapper() throws SQLException {
        return new RowMapper<Empresa>() {
            @Override
            public Empresa mapRow(ResultSet rs, int rowNum) throws SQLException {
                Empresa e = new Empresa();

                e.setIdempresa( rs.getLong("IDEMPRESA") );
                e.setRazaoSocial( rs.getString("RAZAO_SOCIAL") );
                e.setCidade( rs.getString("CIDADE") );
                e.setCnpj( rs.getString("CNPJ") );
                e.setEndereco( rs.getString("ENDERECO") );
                e.setTelefone( rs.getString("TELEFONE") );
                e.setDataCadastro( rs.getDate("DATA_CADASTRO") );
                e.setSituacaoCadastral( rs.getString("SITUACAO_CADASTRAL"));

                return e;
            }
        };
    }

    
    /**
     * Inicializa o dataSource definido na configuração.
     */
    public void setDataSource() {
        SpringJdbcConfig config = new SpringJdbcConfig(env);
        this.jdbcTemplate = new NamedParameterJdbcTemplate( config.postgresDataSource() );
    }
}
