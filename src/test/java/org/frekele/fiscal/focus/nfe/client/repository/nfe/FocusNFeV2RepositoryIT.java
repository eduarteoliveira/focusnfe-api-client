package org.frekele.fiscal.focus.nfe.client.repository.nfe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.frekele.fiscal.focus.nfe.client.auth.EnvironmentFocusNFeEnum;
import org.frekele.fiscal.focus.nfe.client.auth.FocusNFeAuth;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFeFinalidadeEmissaoEnum;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFeIcmsOrigemEnum;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFeIcmsSituacaoTributariaEnum;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFeIncluiNoTotalEnum;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFeModalidadeFreteEnum;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFePisCofinsSituacaoTributariaEnum;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFeTipoDocumentoEnum;
import org.frekele.fiscal.focus.nfe.client.enumeration.NFeUnidadeFederativaEnum;
import org.frekele.fiscal.focus.nfe.client.filter.RequestLoggingFilter;
import org.frekele.fiscal.focus.nfe.client.filter.ResponseLoggingFilter;
import org.frekele.fiscal.focus.nfe.client.model.entities.requisicao.notafiscal.NFeEnvioRequisicaoNotaFiscal;
import org.frekele.fiscal.focus.nfe.client.model.entities.requisicao.notafiscal.NFeItem;
import org.frekele.fiscal.focus.nfe.client.model.request.nfe.body.NFeAutorizarBodyRequest;
import org.frekele.fiscal.focus.nfe.client.model.request.nfe.body.NFeCCeBodyRequest;
import org.frekele.fiscal.focus.nfe.client.model.request.nfe.body.NFeCancelarBodyRequest;
import org.frekele.fiscal.focus.nfe.client.model.request.nfe.body.NFeEmailBodyRequest;
import org.frekele.fiscal.focus.nfe.client.model.request.nfe.body.NFeInutilizarBodyRequest;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.NFeAutorizarResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.NFeCCeResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.NFeCancelarResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.NFeConsultarResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.NFeEmailResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.NFeInutilizarResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.body.NFeAutorizarBodyResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.body.NFeCCeBodyResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.body.NFeCancelarBodyResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.body.NFeConsultarBodyResponse;
import org.frekele.fiscal.focus.nfe.client.model.response.nfe.body.NFeInutilizarBodyResponse;
import org.frekele.fiscal.focus.nfe.client.testng.InvokedMethodListener;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.ws.rs.ForbiddenException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @author frekele - Leandro Kersting de Freitas
 */
@Listeners(InvokedMethodListener.class)
public class FocusNFeV2RepositoryIT {

    private FocusNFeV2Repository repository;

    private ObjectMapper mapper = new ObjectMapper();

    private String cnpjEmitente;

    private String reference;

    @BeforeClass
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        cnpjEmitente = System.getenv("FOCUS_NFE_CNPJ_EMITENTE");
        String accessToken = System.getenv("FOCUS_NFE_ACCESS_TOKEN");
        EnvironmentFocusNFeEnum environment = EnvironmentFocusNFeEnum.HOMOLOGATION;
        FocusNFeAuth auth = FocusNFeAuth.newBuilder()
            .withAccessToken(accessToken)
            .withEnvironment(environment)
            .build();
        ResteasyClient client = new ResteasyClientBuilder()
            .register(RequestLoggingFilter.class)
            .register(ResponseLoggingFilter.class)
            .build();
        repository = new FocusNFeV2RepositoryImpl(client, auth);

        reference = UUID.randomUUID().toString();
        System.out.println("Reference: " + reference);
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        //After Method Sleep 2 seg, for prevent Error.
        this.sleep(2);
    }

    @Test
    public void testAutorizar() throws Exception {
        System.out.println("Reference: " + reference);
        NFeEnvioRequisicaoNotaFiscal nfe = NFeEnvioRequisicaoNotaFiscal.newBuilder()
            .withNaturezaOperacao("VENDA DE MERCADORIA")
            .withDataEmissao(OffsetDateTime.now())
            .withTipoDocumento(NFeTipoDocumentoEnum.NOTA_FISCAL_SAIDA)
            .withFinalidadeEmissao(NFeFinalidadeEmissaoEnum.NOTA_NORMAL)
            .withCnpjEmitente(cnpjEmitente)
            .withNomeDestinatario("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL")
            .withCpfDestinatario("51966818092")
            .withTelefoneDestinatario("5196185555")
            .withLogradouroDestinatario("Av Otto Niemeyer")
            .withNumeroDestinatario("600")
            .withBairroDestinatario("Tristeza")
            .withMunicipioDestinatario("Porto Alegre")
            .withUfDestinatario(NFeUnidadeFederativaEnum.RIO_GRANDE_DO_SUL)
            .withCepDestinatario("91910-001")
            .withModalidadeFrete(NFeModalidadeFreteEnum.POR_CONTA_EMITENTE)
            .withItems(new ArrayList<>())
            .build();
        NFeItem item = NFeItem.newBuilder()
            .withNumeroItem("1")
            .withCodigoProduto("XYZ-12345")
            .withDescricao("Produto Teste 12345 XYZ")
            .withCfop("5102")
            .withCodigoNcm("94036000")
            .withUnidadeComercial("UN")
            .withQuantidadeComercial(BigDecimal.valueOf(1))
            .withValorUnitarioComercial(new BigDecimal("124.35"))
            .withUnidadeTributavel("UN")
            .withQuantidadeTributavel(BigDecimal.valueOf(1))
            .withValorUnitarioTributavel(new BigDecimal("124.35"))
            .withValorBruto(new BigDecimal("124.35"))
            .withIcmsSituacaoTributaria(NFeIcmsSituacaoTributariaEnum.TRIBUTADA_SIMPLES_NACIONAL_SEM_PERMISSAO_CREDITO)
            .withIcmsOrigem(NFeIcmsOrigemEnum.NACIONAL)
            .withPisSituacaoTributaria(NFePisCofinsSituacaoTributariaEnum.OPERACAO_ISENTA_DA_CONTRIBUICAO)
            .withCofinsSituacaoTributaria(NFePisCofinsSituacaoTributariaEnum.OPERACAO_ISENTA_DA_CONTRIBUICAO)
            .withIncluiNoTotal(NFeIncluiNoTotalEnum.SIM)
            .build();
        nfe.getItems().add(item);

        NFeAutorizarBodyRequest bodyRequest = NFeAutorizarBodyRequest.newBuilder().withNfe(nfe).build();
        NFeAutorizarResponse response = repository.autorizar(reference, bodyRequest);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
        NFeAutorizarBodyResponse bodyResponse = response.getBody();
        System.out.println("Body.Status: " + bodyResponse.getStatus());

        //After Method Sleep 10 seg, for prevent Error.
        this.sleep(10);
    }

    @Test(dependsOnMethods = "testAutorizar")
    public void testEmitirCCe() throws Exception {
        System.out.println("Reference: " + reference);
        NFeCCeBodyRequest bodyRequest = NFeCCeBodyRequest.newBuilder()
            .withCorrecao("bla bla bla bla bla bla bla bla bla bla")
            .build();
        NFeCCeResponse response = repository.emitirCCe(reference, bodyRequest);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
        NFeCCeBodyResponse bodyResponse = response.getBody();
        System.out.println("Body.Status: " + bodyResponse.getStatus());
    }

    @Test(dependsOnMethods = "testEmitirCCe")
    public void testEnviarEmail() throws Exception {
        System.out.println("Reference: " + reference);
        NFeEmailBodyRequest bodyRequest = NFeEmailBodyRequest.newBuilder()
            .withEmails("testexyz12345@teste.com.br")
            .build();
        NFeEmailResponse response = repository.enviarEmail(reference, bodyRequest);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
    }

    @Test(dependsOnMethods = "testEnviarEmail")
    public void testCancelar() throws Exception {
        System.out.println("Reference: " + reference);
        NFeCancelarBodyRequest bodyRequest = NFeCancelarBodyRequest.newBuilder()
            .withJustificativa("bla bla bla bla bla bla bla bla bla bla bla bla bla bla")
            .build();
        NFeCancelarResponse response = repository.cancelar(reference, bodyRequest);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
        NFeCancelarBodyResponse bodyResponse = response.getBody();
        System.out.println("Body.Status: " + bodyResponse.getStatus());
        //After Method Sleep 10 seg, for prevent Error.
        this.sleep(10);
    }

    @Test(dependsOnMethods = "testCancelar")
    public void testConsultar() throws Exception {
        System.out.println("Reference: " + reference);
        NFeConsultarResponse response = repository.consultar(reference);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
        NFeConsultarBodyResponse bodyResponse = response.getBody();
        System.out.println("Body.Status: " + bodyResponse.getStatus());
    }

    @Test(dependsOnMethods = "testConsultar")
    public void testConsultarTudo() throws Exception {
        System.out.println("Reference: " + reference);
        NFeConsultarResponse response = repository.consultarTudo(reference);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
        NFeConsultarBodyResponse bodyResponse = response.getBody();
        System.out.println("Body.Status: " + bodyResponse.getStatus());
        System.out.println("Body.Status.ProtocoloNotaFiscal.Motivo: " + bodyResponse.getProtocoloNotaFiscal().getMotivo());
    }

    @Test(dependsOnMethods = "testConsultarTudo")
    public void testInutilizar() throws Exception {
        NFeInutilizarBodyRequest bodyRequest = NFeInutilizarBodyRequest.newBuilder()
            .withCnpj(cnpjEmitente)
            .withSerie("1")
            .withNumeroInicial("2")
            .withNumeroFinal("4")
            .withJustificativa("bla bla bla bla bla bla bla bla bla")
            .build();
        NFeInutilizarResponse response = repository.inutilizar(bodyRequest);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
        NFeInutilizarBodyResponse bodyResponse = response.getBody();
        System.out.println("Body.Status: " + bodyResponse.getStatus());
    }

    @Test(dependsOnMethods = "testInutilizar", expectedExceptions = ForbiddenException.class)
    public void testInutilizarWithError() throws Exception {
        NFeInutilizarBodyRequest bodyRequest = NFeInutilizarBodyRequest.newBuilder()
            .withCnpj("")
            .withSerie("")
            .withNumeroInicial("")
            .withNumeroFinal("")
            .withJustificativa("")
            .build();
        NFeInutilizarResponse response = repository.inutilizar(bodyRequest);
        System.out.println("RateLimitLimit: " + response.getRateLimitLimit());
        System.out.println("RateLimitRemaining: " + response.getRateLimitRemaining());
        System.out.println("RateLimitReset: " + response.getRateLimitReset());
        System.out.println("Status: " + response.getStatus());
        NFeInutilizarBodyResponse bodyResponse = response.getBody();
        System.out.println("Body.Status: " + bodyResponse.getStatus());
    }

    private void sleep(long seconds) {
        try {
            long millis = seconds * 1000;
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
            // Restore interrupted state...
            Thread.currentThread().interrupt();
        }
    }
}