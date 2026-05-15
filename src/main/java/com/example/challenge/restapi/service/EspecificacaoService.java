package com.example.challenge.restapi.service;

import com.example.challenge.restapi.DTOs.request.EspecificacaoRequest;
import com.example.challenge.restapi.DTOs.response.EspecificacaoResponse;
import com.example.challenge.restapi.exception.BusinessException;
import com.example.challenge.restapi.exception.ResourceNotFoundException;
import com.example.challenge.restapi.model.Especificacao;
import com.example.challenge.restapi.model.Veiculo;
import com.example.challenge.restapi.repository.EspecificacaoRepository;
import com.example.challenge.restapi.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EspecificacaoService {
    private final EspecificacaoRepository especificacaoRepository;
    private final VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public List<EspecificacaoResponse> findAll(Long veiculoId) {
        getVeiculoOrThrow(veiculoId);
        return especificacaoRepository.findByVeiculoId(veiculoId)
                .stream().map(EspecificacaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EspecificacaoResponse findById(Long veiculoId, Long especId) {
        return EspecificacaoResponse.from(getEspecOrThrow(veiculoId, especId));
    }

    @Transactional
    public EspecificacaoResponse addOrUpdate(Long veiculoId, EspecificacaoRequest request) {
        Veiculo veiculo = getVeiculoOrThrow(veiculoId);

        Especificacao espec = especificacaoRepository
                .findByVeiculoIdAndAtributoIgnoreCase(veiculoId, request.atributo())
                .orElseGet(() -> Especificacao.builder()
                        .veiculo(veiculo)
                        .atributo(request.atributo())
                        .build());

        espec.setValor(request.valor());
        return EspecificacaoResponse.from(especificacaoRepository.save(espec));
    }

    @Transactional
    public void delete(Long veiculoId, Long especId) {
        Especificacao espec = getEspecOrThrow(veiculoId, especId);
        especificacaoRepository.delete(espec);
        log.info("Especificacao id={} removida do veículo id={}", especId, veiculoId);
    }

    private Veiculo getVeiculoOrThrow(Long veiculoId) {
        return veiculoRepository.findById(veiculoId)
                .orElseThrow(() -> new ResourceNotFoundException("Veículo não encontrado com id=" + veiculoId));
    }

    private Especificacao getEspecOrThrow(Long veiculoId, Long especId) {
        Especificacao e = especificacaoRepository.findById(especId)
                .orElseThrow(() -> new ResourceNotFoundException("Especificação não encontrada com id=" + especId));
        if (!e.getVeiculo().getId().equals(veiculoId)) {
            throw new BusinessException("Especificação id=%d não pertence ao veículo id=%d.".formatted(especId, veiculoId));
        }
        return e;
    }
}