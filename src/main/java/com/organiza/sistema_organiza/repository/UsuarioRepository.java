package com.organiza.sistema_organiza.repository;

import com.organiza.sistema_organiza.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByEmail(String email);
    
    // Validar o código de inativação (TC_017, TC_018)
    Optional<Usuario> findByEmailAndCodigoVerificacao(String email, String codigoVerificacao);
}