package com.example.challenge.restapi.service;

import com.example.challenge.restapi.DTOs.response.ComparacaoResponse;
import com.example.challenge.restapi.DTOs.request.VeiculoRequest;
import com.example.challenge.restapi.DTOs.response.VeiculoResponse;
import com.example.challenge.restapi.exception.BusinessException;
import com.example.challenge.restapi.exception.ResourceNotFoundException;
import com.example.challenge.restapi.model.Especificacao;
import com.example.challenge.restapi.model.Veiculo;
import com.example.challenge.restapi.repository.EspecificacaoRepository;
import com.example.challenge.restapi.repository.VeiculoRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VeiculoService {
    private final VeiculoRepository veiculoRepository;
    private final EspecificacaoRepository especificacaoRepository;
    private final EntityManager entityManager;

    @Transactional
    public VeiculoResponse create(VeiculoRequest request) {
        log.debug("Criando veiculo: {}/{}/{}", request.marca(), request.modelo(), request.versao());

        if (veiculoRepository.existsByMarcaAndModeloAndVersao(
                request.marca(), request.modelo(), request.versao())) {
            throw new BusinessException(
                    "Ja existe um veiculo cadastrado com marca='%s', modelo='%s', versao='%s'."
                            .formatted(request.marca(), request.modelo(), request.versao()));
        }

        Veiculo veiculo = Veiculo.builder()
                .marca(request.marca())
                .modelo(request.modelo())
                .versao(request.versao())
                .ano(request.ano())
                .build();

        if (request.especificacoes() != null) {
            request.especificacoes().forEach(esp ->
                    veiculo.getEspecificacoes().add(
                            Especificacao.builder()
                                    .veiculo(veiculo)
                                    .atributo(esp.atributo())
                                    .valor(esp.valor())
                                    .build()));
        }

        Veiculo saved = veiculoRepository.save(veiculo);
        log.info("Veiculo criado com id={}", saved.getId());
        return VeiculoResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public Page<VeiculoResponse> listAll(Pageable pageable) {
        return veiculoRepository.findAll(pageable).map(VeiculoResponse::from);
    }

    @Transactional(readOnly = true)
    public VeiculoResponse findById(Long id) {
        return VeiculoResponse.from(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Page<VeiculoResponse> search(String marca, String modelo, String versao, Pageable pageable) {
        return veiculoRepository.search(marca, modelo, versao, pageable).map(VeiculoResponse::from);
    }

    @Transactional
    public VeiculoResponse update(Long id, VeiculoRequest request) {
        Veiculo veiculo = getOrThrow(id);

        veiculoRepository.findByMarcaAndModeloAndVersao(
                        request.marca(), request.modelo(), request.versao())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new BusinessException(
                                "Ja existe outro veiculo com marca='%s', modelo='%s', versao='%s'."
                                        .formatted(request.marca(), request.modelo(), request.versao()));
                    }
                });

        veiculo.setMarca(request.marca());
        veiculo.setModelo(request.modelo());
        veiculo.setVersao(request.versao());
        veiculo.setAno(request.ano());

        especificacaoRepository.deleteByVeiculoId(id);
        entityManager.flush();

        veiculo.getEspecificacoes().clear();

        if (request.especificacoes() != null) {
            request.especificacoes().forEach(esp ->
                    veiculo.getEspecificacoes().add(
                            Especificacao.builder()
                                    .veiculo(veiculo)
                                    .atributo(esp.atributo())
                                    .valor(esp.valor())
                                    .build()));
        }

        return VeiculoResponse.from(veiculoRepository.save(veiculo));
    }

    @Transactional
    public void delete(Long id) {
        Veiculo veiculo = getOrThrow(id);
        veiculoRepository.delete(veiculo);
        log.info("Veiculo id={} removido", id);
    }

    @Transactional(readOnly = true)
    public ComparacaoResponse comparar(List<Long> ids) {
        if (ids == null || ids.size() < 2) {
            throw new BusinessException("Informe ao menos 2 IDs para comparacao.");
        }

        List<Veiculo> veiculos = ids.stream().map(this::getOrThrow).toList();

        LinkedHashSet<String> atributos = new LinkedHashSet<>();
        veiculos.forEach(v -> v.getEspecificacoes().forEach(e -> atributos.add(e.getAtributo())));

        Map<String, List<String>> tabela = new LinkedHashMap<>();
        for (String atributo : atributos) {
            List<String> valores = veiculos.stream().map(v ->
                    v.getEspecificacoes().stream()
                            .filter(e -> e.getAtributo().equalsIgnoreCase(atributo))
                            .map(e -> e.getValor() != null ? e.getValor() : "Nao disponivel")
                            .findFirst()
                            .orElse("Nao disponivel")
            ).toList();
            tabela.put(atributo, valores);
        }

        List<String> labels = veiculos.stream()
                .map(v -> v.getMarca() + " " + v.getModelo() + " " + v.getVersao())
                .toList();

        return new ComparacaoResponse(labels, tabela);
    }

    private Veiculo getOrThrow(Long id) {
        return veiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veiculo nao encontrado com id=" + id));
    }
}