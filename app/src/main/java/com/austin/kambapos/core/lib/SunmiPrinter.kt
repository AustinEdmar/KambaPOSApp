// (1) Define o pacote da classe — organiza o código dentro do módulo do projecto
package com.austin.kambapos.core.lib

// (2) Importa o Context do Android — necessário para Toast, Resources e bindService
import android.content.Context
// (3) Importa todo o pacote gráfico do Android — Bitmap, Canvas, Paint, Color, Typeface, BitmapFactory
import android.graphics.*
// (4) Importa a exceção lançada quando a comunicação com o serviço da impressora falha
import android.os.RemoteException
// (5) Importa o Toast — usado para mostrar mensagens rápidas ao utilizador no ecrã
import android.widget.Toast
// (6) Importa State — tipo de estado observável somente-leitura, exposto para o Compose
import androidx.compose.runtime.State
// (7) Importa mutableStateOf — cria um estado reactivo que o Compose detecta e re-renderiza automaticamente
import androidx.compose.runtime.mutableStateOf
import com.austin.kambapos.data.model.ReceiptCompany
// (8) Importa o modelo de item do carrinho usado no recibo
import com.austin.kambapos.data.model.ReceiptModel
// (9) Importa toda a SDK da impressora Sunmi — SunmiPrinterService, InnerPrinterManager, callbacks, etc.
import com.sunmi.peripheral.printer.*
// (10) Importa DateFormat — formata a data actual num texto legível como "04/04/2026 10:30"
import java.text.DateFormat
// (11) Importa Date — representa o momento actual do sistema (data e hora)
import java.util.Date
// Importamos os utilitários de moeda
import com.austin.kambapos.utils.parseKwanzaToDouble
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.util.Locale
import kotlin.coroutines.resume

// (12) Declara a classe SunmiPrinter recebendo o Context no construtor.
//      O "private val" guarda o context como propriedade privada acessível em toda a classe.
class SunmiPrinter(private val context: Context) {

    // ════════════════════════════════════════════════════════
    // BLOCO A — ESTADO INTERNO DA IMPRESSORA
    // ════════════════════════════════════════════════════════

    // (13) Guarda a referência ao serviço da impressora Sunmi.
    //      Começa como null porque a ligação ainda não foi feita.
    //      É preenchida quando a impressora conecta e limpa quando desconecta.
    private var printerService: SunmiPrinterService? = null

    // (14) Estado interno mutável — false = desconectada, true = conectada.
    //      O "_" (underscore) é convenção para propriedades privadas e mutáveis.
    private val _isConnected = mutableStateOf(false)

    // (15) Versão pública e somente-leitura de _isConnected.
    //      Quem usar esta classe pode observar o estado de conexão na UI sem modificá-lo.
    val isConnected: State<Boolean> = _isConnected


    // ════════════════════════════════════════════════════════
    // BLOCO B — CALLBACK DE CONEXÃO DA IMPRESSORA
    // ════════════════════════════════════════════════════════

    // (16) Cria um objecto anónimo que implementa InnerPrinterCallback.
    //      O sistema Sunmi chama este callback automaticamente quando o estado
    //      da impressora muda (conectou ou desconectou).
    private val printerCallback = object : InnerPrinterCallback() {

        // (17) Chamado automaticamente pelo sistema quando a impressora conecta com sucesso.
        override fun onConnected(service: SunmiPrinterService?) {
            // (17.1) Guarda o serviço recebido — é através dele que todos os comandos são enviados
            printerService = service
            // (17.2) Actualiza o estado reactivo para true → a UI do Compose detecta e actualiza
            _isConnected.value = true
            // (17.3) Mostra mensagem de confirmação ao utilizador
            Toast.makeText(context, "Impressora conectada!", Toast.LENGTH_SHORT).show()
        }

        // (18) Chamado automaticamente quando a impressora perde a ligação.
        override fun onDisconnected() {
            // (18.1) Remove a referência ao serviço para evitar usar um objecto inválido
            printerService = null
            // (18.2) Actualiza o estado para false → a UI reflecte que está desconectada
            _isConnected.value = false
            // (18.3) Avisa o utilizador que a impressora desconectou
            Toast.makeText(context, "Impressora desconectada", Toast.LENGTH_SHORT).show()
        }
    }


    // ════════════════════════════════════════════════════════
    // BLOCO C — CALLBACK DE RESULTADO DA IMPRESSÃO
    // ════════════════════════════════════════════════════════

    // (19) Cria um objecto anónimo que implementa InnerResultCallback.
    //      Este callback é passado aos métodos de impressão para receber
    //      notificações sobre o sucesso ou falha de cada operação.
    private val printCallback = object : InnerResultCallback() {

        // (20) Chamado quando o comando de impressão foi executado.
        //      isSuccess = true significa que o comando chegou à impressora sem erros.
        override fun onRunResult(isSuccess: Boolean) {
            val msg = if (isSuccess) "Impressao enviada com sucesso" else "Falha ao enviar impressao"
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        // (21) Chamado quando a impressora retorna uma string de dados (ex: leitura de sensor).
        //      Deixado vazio pois não é necessário neste fluxo.
        override fun onReturnString(result: String?) {
            // Dados retornados (se houver)
        }

        // (22) Chamado quando a impressora lança uma excepção interna.
        //      code = código do erro, msg = descrição do problema.
        override fun onRaiseException(code: Int, msg: String?) {
            Toast.makeText(context, "Erro na impressao: $msg", Toast.LENGTH_LONG).show()
        }

        // (23) Chamado quando a impressora conclui fisicamente a impressão.
        //      code == 0 significa sucesso; qualquer outro valor indica erro.
        override fun onPrintResult(code: Int, msg: String?) {
            if (code == 0) {
                Toast.makeText(context, "Impressao concluida!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Erro na impressao: $msg", Toast.LENGTH_LONG).show()
            }
        }
    }


    // ════════════════════════════════════════════════════════
    // BLOCO D — GESTÃO DE CONEXÃO
    // ════════════════════════════════════════════════════════

    // (24) Função pública para iniciar a ligação com a impressora Sunmi.
    //      Deve ser chamada no LaunchedEffect(Unit) do Composable que usa a impressora.
    fun connectPrinter() {
        try {
            // (24.1) Obtém a instância única do gestor de impressoras (Singleton)
            // (24.2) Chama bindService passando o context e o callback de conexão.
            //        Retorna true se o pedido foi aceite, false se falhou imediatamente.
            val result = InnerPrinterManager
                .getInstance()
                .bindService(context, printerCallback)

            // (24.3) Se o bindService retornou false, a ligação não foi iniciada → avisa o utilizador
            if (!result) {
                Toast.makeText(context, "Falha ao iniciar conexao", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // (24.4) Captura qualquer erro inesperado durante o processo de ligação
            Toast.makeText(context, "Erro ao conectar: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // (25) Função pública para encerrar a ligação com a impressora.
    //      Deve ser chamada no DisposableEffect(onDispose) do Composable para libertar recursos.
    fun disconnect() {
        try {
            // (25.1) Obtém o gestor e chama unBindService para terminar a ligação correctamente
            InnerPrinterManager
                .getInstance()
                .unBindService(context, printerCallback)
        } catch (e: Exception) {
            // (25.2) Captura erros silenciosamente para não bloquear o encerramento da app
            e.printStackTrace()
        }
    }

    // (26) Verifica o estado actual da impressora e sincroniza o estado reactivo.
    fun checkStatus() {
        if (printerService != null) {
            // (26.1) Serviço existe → impressora ligada; actualiza estado e informa o utilizador
            _isConnected.value = true
            Toast.makeText(context, "Impressora esta conectada e operacional", Toast.LENGTH_SHORT).show()
        } else {
            // (26.2) Serviço é null → impressora desligada; actualiza o estado
            _isConnected.value = false
            Toast.makeText(context, "Impressora nao esta conectada", Toast.LENGTH_SHORT).show()
            // (26.3) Tenta reconectar automaticamente
            connectPrinter()
        }
    }


    // ════════════════════════════════════════════════════════
    // BLOCO E — GUARD HELPER (PROTECÇÃO)
    // ════════════════════════════════════════════════════════

    // (27) Função privada auxiliar que protege qualquer operação de impressão.
    //      Evita repetir o mesmo bloco "if (printerService == null)" em todos os métodos.
    //      Recebe uma lambda "action" e só a executa se a impressora estiver conectada.
    private fun requirePrinter(action: () -> Unit) {
        if (printerService == null) {
            // (27.1) Serviço inexistente → avisa o utilizador e interrompe sem executar a acção
            Toast.makeText(context, "Impressora nao conectada", Toast.LENGTH_SHORT).show()
            return
        }
        // (27.2) Impressora disponível → executa o bloco de código passado como parâmetro
        action()
    }


    // ════════════════════════════════════════════════════════
    // BLOCO F — IMPRESSÕES DE ALTO NÍVEL
    // ════════════════════════════════════════════════════════

    // (28) Imprime uma página de teste completa com logo, data e status.
    //      "= requirePrinter { }" garante que só executa se a impressora estiver conectada.
    fun printTest() = requirePrinter {
        try {
            // (28.1) Reinicia a impressora para um estado limpo antes de imprimir
            printerService?.printerInit(printCallback)
            // (28.2) Imprime o logo e o nome da loja no topo
            printLogo()
            // (28.3) Imprime o cabeçalho do teste
            printerService?.printText("=== TESTE DE IMPRESSAO ===\n", null)
            // (28.4) Imprime a data e hora actuais do sistema
            printerService?.printText("Data: ${Date()}\n", null)
            // (28.5) Imprime o status da impressora
            printerService?.printText("Status: OK\n", null)
            // (28.6) Imprime o rodapé de encerramento do teste
            printerService?.printText("==========================\n", null)
            // (28.7) Avança 2 linhas em branco para o papel não ficar cortado no texto
            printerService?.lineWrap(2, null)
        } catch (e: RemoteException) {
            // (28.8) Captura erros de comunicação remota com a impressora
            Toast.makeText(context, "Erro ao imprimir: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    // (29) Imprime um recibo de venda completo e formatado profissionalmente.
    //      items    → lista de produtos do carrinho
    //      total    → valor total já com IVA (calculado fora)
    //      tableId  → número da mesa
    //      orderId  → identificador do pedido (pode ser null)
    fun printReceipt(
        receipt: ReceiptModel,
        onDone: (() -> Unit)? = null
    ) = requirePrinter {
        try {
            printerService?.printerInit(null)
            printerService?.enterPrinterBuffer(true)

            // ── CABEÇALHO: empresa ──
            printLogo(company = receipt.company)
            receipt.company?.phone?.takeIf { it.isNotBlank() }?.let {
                printTextWithSpacing("Tlf: $it", bottomSpacing = 0, alignment = 1)
            }
            receipt.company?.nif?.takeIf { it.isNotBlank() }?.let {
                printTextWithSpacing("NIF: $it", bottomSpacing = 1, alignment = 1)
            }

            // ── DOCUMENTO ──
            val title = when (receipt.documentType) {
                "FT" -> "FACTURA"
                "TV" -> "TALAO DE VENDA"
                else -> "FACTURA / RECIBO"
            }
            addSeparatorLine("=", 32)
            printTextWithSpacing(title, bottomSpacing = 0, alignment = 1)
            receipt.documentNumber?.let {
                printTextWithSpacing("N.º $it", bottomSpacing = 0, alignment = 1)
            }
            printKeyValue("Data", receipt.issuedAt ?: DateFormat.getDateTimeInstance().format(Date()))
            addSeparatorLine("=", 32)

            receipt.orderId?.let {
                printBitmapWithSpacing(
                    bitmap = createTextBitmap("PEDIDO N.º $it", 38, bold = true),
                    topSpacing = 0, bottomSpacing = 1, alignment = 1
                )
            }

            receipt.operatorName?.let { printKeyValue("Operador", it.take(22)) }
            receipt.terminal?.takeIf { it.isNotBlank() }?.let { printKeyValue("Terminal", it.take(20)) }

            // ── CLIENTE ──
            addSeparatorLine("-", 32)
            printKeyValue("Cliente", receipt.customerName.take(24))
            printKeyValue("NIF", receipt.customerTaxNumber)
            receipt.customerAddress?.takeIf { it.isNotBlank() }?.let {
                printTextWithSpacing(it, bottomSpacing = 0)
            }

            // ── ITENS ──
            addSeparatorLine("-", 32)
            printTextWithSpacing(itemRow("QTD", "IVA", "UNIT", "TOTAL"), bottomSpacing = 0)
            addSeparatorLine("-", 32)

            receipt.lines.forEach { l ->
                val head = listOfNotNull(l.code?.takeIf { it.isNotBlank() }, l.name).joinToString(" ")
                printTextWithSpacing(head, bottomSpacing = 0)
                printTextWithSpacing(
                    itemRow(qtyText(l.quantity), "${l.taxRate.toInt()}%", money(l.unitPrice), money(l.net)),
                    bottomSpacing = 1
                )
            }

            // ── TOTAIS ──
            addSeparatorLine("-", 32)
            printKeyValue("Subtotal", "${money(receipt.subtotal)} kz")
            if (receipt.discount > 0.0) printKeyValue("Desconto", "${money(receipt.discount)} kz")
            printKeyValue("IVA", "${money(receipt.tax)} kz")
            addSeparatorLine("=", 32)
            printBitmapWithSpacing(
                bitmap = createTextBitmap("TOTAL ${money(receipt.total)} kz", 30, bold = true),
                topSpacing = 1, bottomSpacing = 1, alignment = 1
            )
            addSeparatorLine("=", 32)

            // ── RESUMO POR TAXA ──
            if (receipt.taxSummary.isNotEmpty()) {
                printTextWithSpacing(
                    "Taxa".padEnd(7) + "Incid.".padStart(13) + "IVA".padStart(12),
                    bottomSpacing = 0
                )
                receipt.taxSummary.forEach { t ->
                    printTextWithSpacing(
                        "${t.rate.toInt()}%".padEnd(7) + money(t.base).padStart(13) + money(t.tax).padStart(12),
                        bottomSpacing = 0
                    )
                }
                addSpacing(1)
                addSeparatorLine("-", 32)
            }

            // ── PAGAMENTO ──
            val paymentText = when (receipt.paymentMethod) {
                "cash" -> "Dinheiro"
                "card" -> "Cartão"
                "qrcode" -> "QR Code"
                else -> receipt.paymentMethod
            }
            printKeyValue("Pago a", paymentText)
            if (receipt.paymentMethod == "cash") {
                receipt.paidAmount?.let { printKeyValue("Entregue", "${money(it)} kz") }
                receipt.change?.let { printKeyValue("Troco", "${money(it)} kz") }
            }
            printKeyValue("Itens", receipt.itemsCount.toString())

            // ── LEGAL / FISCAL ──
            receipt.legalText?.takeIf { it.isNotBlank() }?.let {
                printTextWithSpacing(it, topSpacing = 1, bottomSpacing = 1, alignment = 1)
            }
            receipt.hash4?.takeIf { it.isNotBlank() }?.let {
                printTextWithSpacing("$it-Processado por programa validado", bottomSpacing = 0, alignment = 1)
                printTextWithSpacing("n. ${receipt.company?.certificateNumber ?: "-"}", bottomSpacing = 1, alignment = 1)
            }

            // ── QR CODE (nível M, conforme a AGT) ──
            receipt.qrContent?.takeIf { it.isNotBlank() }?.let { content ->
                printerService?.setAlignment(1, null)
                printQRCode(content, moduleSize = 6, errorLevel = 1)   // 1 = M
                printerService?.setAlignment(0, null)
            }

            // ── CÓDIGO DE BARRAS DO PEDIDO ──
            receipt.orderId?.let {
                printerService?.setAlignment(1, null)
                printBarcode(it.toString().padStart(8, '0'), height = 60)
                printerService?.setAlignment(0, null)
            }

            printTextWithSpacing("Obrigado pela preferencia!", topSpacing = 1, bottomSpacing = 0, alignment = 1)
            printTextWithSpacing("Volte sempre", bottomSpacing = 1, alignment = 1)
            printTextWithSpacing("=== ${receipt.copyType} ===", bottomSpacing = 4, alignment = 1)

            if (onDone != null) {
                printerService?.exitPrinterBufferWithCallback(true, object : InnerResultCallback() {
                    override fun onRunResult(isSuccess: Boolean) {}
                    override fun onReturnString(result: String?) {}
                    override fun onRaiseException(code: Int, msg: String?) { onDone() }
                    override fun onPrintResult(code: Int, msg: String?) { onDone() }
                })
            } else {
                printerService?.exitPrinterBuffer(true)
            }

            Toast.makeText(context, "Recibo enviado!", Toast.LENGTH_SHORT).show()

        } catch (e: RemoteException) {
            Toast.makeText(context, "Erro ao imprimir recibo: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
            onDone?.invoke()
        }
    }

    // Suspende até a impressora terminar (ou até ao limite, se algo falhar)
    suspend fun printReceiptAndWait(receipt: ReceiptModel, timeoutMs: Long = 20_000) {
        withTimeoutOrNull(timeoutMs) {
            suspendCancellableCoroutine<Unit> { cont ->
                printReceipt(receipt) { if (cont.isActive) cont.resume(Unit) }
            }
        }
    }

    // (30) Teste de impressão com imagem — imprime o logo seguido de uma linha de texto.
    fun printImageTest() = requirePrinter {
        try {
            // (30.1) Reinicia a impressora antes de imprimir
            printerService?.printerInit(null)
            // (30.2) Imprime o logo (bitmap) como primeira parte do teste
            printLogo()
            // (30.3) Imprime uma linha de texto de confirmação após a imagem
            printerService?.printText("Teste de impressao com imagem!\n", null)
            // (30.4) Avança 2 linhas para o papel não ficar cortado no texto
            printerService?.lineWrap(2, null)
        } catch (e: RemoteException) {
            Toast.makeText(context, "Erro ao imprimir imagem", Toast.LENGTH_SHORT).show()
        }
    }

    // (31) Teste de impressão de QR Code.
    //      content: a string a codificar no QR (padrão: URL de exemplo).
    fun printQRTest(content: String = "https://www.example.com") = requirePrinter {
        // (31.1) Delega a impressão para o método primitivo printQRCode
        printQRCode(content)
        // (31.2) Informa o utilizador que o QR Code foi enviado para impressão
        Toast.makeText(context, "QR Code enviado para impressao", Toast.LENGTH_SHORT).show()
    }


    // ════════════════════════════════════════════════════════
    // BLOCO G — IMPRESSÃO DO LOGO
    // ════════════════════════════════════════════════════════

    // (32) Imprime o logo da loja no topo do documento.
    //      logoResId: ID do drawable a usar como imagem; se null, só imprime o nome em texto.
    fun printLogo(logoResId: Int? = null, company: ReceiptCompany? = null) {
        try {
            logoResId?.let { resId ->
                val resources = context.resources ?: return@let
                val logoBitmap = BitmapFactory.decodeResource(resources, resId)
                logoBitmap?.let {
                    val resized = resizeBitmap(it, 200, 100)
                    printBitmapWithSpacing(
                        bitmap = resized,
                        topSpacing = 1,
                        bottomSpacing = 2,
                        alignment = 1,
                        callback = printCallback
                    )
                }
            }

            // nome e morada vêm do servidor; os valores fixos só servem de reserva
            val name = company?.name ?: "KambaPOS - COMERCIO, LDA"
            val nameSize = if (name.length > 24) 22 else 30   // papel ~384 px: nomes longos cortavam
            printBitmapWithSpacing(
                bitmap = createTextBitmap(name, nameSize, bold = true),
                topSpacing = 0,
                bottomSpacing = 1,
                alignment = 1
            )

            val address = company?.address ?: "Rua Amilcar Cabral, n252"
            printBitmapWithSpacing(
                bitmap = createTextBitmap(address, 22, bold = true),
                topSpacing = 0,
                bottomSpacing = 2,
                alignment = 1
            )
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    // ════════════════════════════════════════════════════════
    // BLOCO H — PRIMITIVOS DE IMPRESSÃO
    // ════════════════════════════════════════════════════════

    // (33) Imprime um QR Code com o conteúdo fornecido.
    //      moduleSize: tamanho de cada módulo do QR em pixels (1-16, padrão 6).
    //      errorLevel: nível de correcção de erros (0=L, 1=M, 2=Q, 3=H, padrão 3).
    fun printQRCode(content: String, moduleSize: Int = 6, errorLevel: Int = 3) {
        try {
            // (33.1) Envia o comando de QR Code para a impressora com callback de resultado
            printerService?.printQRCode(content, moduleSize, errorLevel, printCallback)
            // (33.2) Avança 2 linhas após o QR Code para separar do próximo conteúdo
            printerService?.lineWrap(2, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // (34) Imprime um código de barras com o conteúdo fornecido.
    //      symbology: tipo de código de barras (padrão 8 = CODE128).
    //      height: altura em pixels (padrão 162). width: largura de cada barra (padrão 2).
    //      textPosition: posição do texto (0=none, 1=acima, 2=abaixo, 3=ambos).
    fun printBarcode(
        content: String,
        symbology: Int = 8,
        height: Int = 162,
        width: Int = 2,
        textPosition: Int = 2
    ) {
        try {
            // (34.1) Envia o comando de código de barras para a impressora com todos os parâmetros
            printerService?.printBarCode(content, symbology, height, width, textPosition, printCallback)
            // (34.2) Avança 2 linhas após o código de barras
            printerService?.lineWrap(2, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // (35) Avança o papel um número específico de linhas em branco.
    //      lines: quantas linhas em branco adicionar (padrão 1).
    fun addSpacing(lines: Int = 1) {
        try {
            // (35.1) Envia o comando lineWrap para a impressora avançar o papel
            printerService?.lineWrap(lines, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // (36) Imprime uma linha separadora feita de um caractere repetido.
    //      character: o caractere a repetir (padrão "-").
    //      length: quantas vezes repetir (padrão 32 = largura padrão do papel térmico).
    fun addSeparatorLine(character: String = "-", length: Int = 32) {
        try {
            // (36.1) Repete o caractere "length" vezes, forma a linha e imprime com quebra no final
            printerService?.printText("${character.repeat(length)}\n", null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // (37) Imprime texto com controlo total de espaçamento e alinhamento.
    //      text: conteúdo a imprimir.
    //      topSpacing: linhas em branco antes do texto (padrão 0).
    //      bottomSpacing: linhas em branco depois do texto (padrão 1).
    //      alignment: 0 = esquerda, 1 = centro, 2 = direita (padrão 0).
    fun printTextWithSpacing(
        text: String,
        topSpacing: Int = 0,
        bottomSpacing: Int = 1,
        alignment: Int = 0
    ) {
        try {
            // (37.1) Adiciona linhas em branco acima do texto se topSpacing > 0
            if (topSpacing > 0) addSpacing(topSpacing)
            // (37.2) Define o alinhamento do texto na impressora antes de imprimir
            printerService?.setAlignment(alignment, null)
            // (37.3) Imprime o texto com quebra de linha "\n" no final
            printerService?.printText("$text\n", null)
            // (37.4) Adiciona linhas em branco abaixo do texto se bottomSpacing > 0
            if (bottomSpacing > 0) addSpacing(bottomSpacing)
            // (37.5) Repõe o alinhamento para esquerda (0) para não afectar o próximo conteúdo
            if (alignment != 0) printerService?.setAlignment(0, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    // (38) Imprime um Bitmap com controlo de espaçamento e alinhamento.
    //      Funciona de forma idêntica ao printTextWithSpacing, mas para imagens.
    //      callback: callback opcional para receber o resultado desta impressão específica.
    fun printBitmapWithSpacing(
        bitmap: Bitmap,
        topSpacing: Int = 0,
        bottomSpacing: Int = 1,
        alignment: Int = 0,
        callback: InnerResultCallback? = null
    ) {
        try {
            // (38.1) Adiciona linhas em branco acima da imagem se topSpacing > 0
            if (topSpacing > 0) addSpacing(topSpacing)
            // (38.2) Define o alinhamento da imagem na impressora antes de enviar
            printerService?.setAlignment(alignment, null)
            // (38.3) Envia o bitmap para a impressora com o callback de resultado
            printerService?.printBitmap(bitmap, callback)
            // (38.4) Adiciona linhas em branco abaixo da imagem se bottomSpacing > 0
            if (bottomSpacing > 0) addSpacing(bottomSpacing)
            // (38.5) Repõe o alinhamento para esquerda (0) para não afectar o próximo conteúdo
            if (alignment != 0) printerService?.setAlignment(0, null)
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    // ════════════════════════════════════════════════════════
    // BLOCO I — HELPERS DE FORMATAÇÃO DO RECIBO
    // ════════════════════════════════════════════════════════

    // (39) Imprime uma linha with chave à esquerda e valor à direita, preenchendo o espaço entre eles.
    //      Exemplo: "Mesa              3"
    //      key:   texto da etiqueta (ex: "Mesa", "Data", "Pedido #")
    //      value: valor a mostrar à direita (ex: "3", "04/04/2026")
    private fun printKeyValue(key: String, value: String) {
        // (39.1) Largura total do papel térmico em caracteres
        val totalWidth = 32
        // (39.2) Calcula quantos espaços são necessários entre a chave e o valor
        //        para que o valor fique encostado à margem direita
        val spaces = totalWidth - key.length - value.length
        // (39.3) Monta a linha: chave + espaços + valor (mínimo 1 espaço para não colidir)
        val line = key + " ".repeat(maxOf(1, spaces)) + value
        // (39.4) Imprime a linha sem espaçamento extra (bottomSpacing = 0)
        printTextWithSpacing(text = line, bottomSpacing = 0)
    }

    // (39.5) Overload conveniente de printKeyValue que aceita Double directamente.
    //        Formata o valor em Kz e delega para o método principal.
    private fun printKeyValue(key: String, value: Double) {
        printKeyValue(key, "${formatKz(value)} kz")
    }

    // (40) Distribui 5 colunas de texto numa linha de 32 caracteres de largura.
    //      Usado para criar a tabela de itens do recibo com colunas alinhadas.
    //      col1: nome do item (alinhado à esquerda, 8 chars)
    //      col2: quantidade   (alinhada à direita, 3 chars)
    //      col3: IVA %        (alinhada à direita, 7 chars)
    //      col4: preço unit.  (alinhado à direita, 7 chars)
    //      col5: total linha  (alinhado à direita, 7 chars) — soma = 32 chars
    private fun padColumns(col1: String, col2: String, col3: String, col4: String, col5: String): String {
        val c1 = col1.padEnd(8)   // nome — 8 chars à esquerda
        val c2 = col2.padStart(3)  // qtd  — 3 chars
        val c3 = col3.padStart(7)  // IVA  — 7 chars
        val c4 = col4.padStart(7)  // unit — 7 chars
        val c5 = col5.padStart(7)  // total— 7 chars

        return "$c1$c2$c3$c4$c5"   // total = 32 chars → cabe nos 32 do papel térmico
    }

    // (41) ✅ Formata um Int com separador de milhar no estilo angolano (ponto).
    //      Exemplos: 3000 → "3.000"   |   1500 → "1.500"   |   800 → "800"
    private fun formatKz(value: Int?): String =
    // (41.1) String.format com "%,d" usa vírgula como separador de milhar por padrão
        //        .replace(",", ".") converte para o estilo angolano com ponto
        String.format("%,d", value ?: 0).replace(",", ".")

    // (41.2) ✅ Overload para Double — necessário para subtotal, tax, total, paidAmount, change.
    //        Formata com 2 casas decimais e separador de milhar angolano.
    //        Exemplos: 7450.0 → "7.450,00"   |   1011.5 → "1.011,50"






    // "%,.2f" depende do idioma do telemóvel (num telemóvel em inglês dava "1.299.00").
// Locale.US fixa o formato e depois trocamos para o estilo angolano: 1.299,00
    private fun money(value: Double): String =
        String.format(Locale.US, "%,.2f", value)
            .replace(",", "#").replace(".", ",").replace("#", ".")

    private fun formatKz(value: Double?): String = money(value ?: 0.0)

    private fun qtyText(q: Double): String =
        if (q % 1.0 == 0.0) q.toInt().toString() else String.format(Locale.US, "%.3f", q).replace(".", ",")

    // Linha de item em 32 colunas: QTD(6) IVA(5) UNIT(10) TOTAL(11)
    private fun itemRow(qty: String, iva: String, unit: String, total: String): String =
        qty.padEnd(6) + iva.padEnd(5) + unit.padStart(10) + total.padStart(11)

    // ════════════════════════════════════════════════════════
    // BLOCO J — UTILITÁRIOS DE BITMAP
    // ════════════════════════════════════════════════════════

    // (42) Redimensiona um Bitmap para as dimensões especificadas.
    //      Usado para ajustar o logo ao tamanho ideal antes de imprimir.
    //      original: bitmap de origem. newWidth/newHeight: novas dimensões em pixels.
    fun resizeBitmap(original: Bitmap, newWidth: Int, newHeight: Int): Bitmap =
        // (42.1) Cria um novo bitmap escalado — "false" desactiva o filtro bilinear (mais rápido)
        Bitmap.createScaledBitmap(original, newWidth, newHeight, false)

    // (43) Cria um Bitmap com um texto renderizado dentro dele.
    //      Necessário para imprimir texto como imagem (logos, cabeçalhos com fonte especial).
    //      text: texto a desenhar. textSize: tamanho em pixels. bold: negrito ou não.
    fun createTextBitmap(text: String, textSize: Int, bold: Boolean = false): Bitmap {

        // (43.1) Configura o Paint — a "caneta" que define como o texto será desenhado
        val paint = Paint().apply {
            color = Color.BLACK                  // (43.2) Cor do texto: preto (necessário para impressora térmica)
            this.textSize = textSize.toFloat()   // (43.3) Tamanho da fonte convertido para Float
            isAntiAlias = true                   // (43.4) Suaviza as bordas do texto para melhor qualidade
            textAlign = Paint.Align.CENTER       // (43.5) Texto alinhado ao centro horizontalmente no canvas
            // (43.6) Fonte negrito se bold=true, normal caso contrário
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }

        // (43.7) Obtém as métricas da fonte — contém top, bottom, ascent, descent do texto
        val metrics = paint.fontMetrics
        // (43.8) Mede a largura real do texto em pixels com o Paint configurado
        val width = paint.measureText(text).toInt()
        // (43.9) Calcula a altura total necessária com base nas métricas da fonte
        val height = (metrics.bottom - metrics.top).toInt()

        // (43.10) Cria o Bitmap with padding extra (+20 largura, +10 altura) para não cortar as bordas
        //         ARGB_8888 = máxima qualidade de cor (8 bits por canal: Alpha, Red, Green, Blue)
        val bitmap = Bitmap.createBitmap(width + 20, height + 10, Bitmap.Config.ARGB_8888)

        // (43.11) Cria um Canvas sobre o bitmap — é a "folha" onde o texto será desenhado
        Canvas(bitmap).apply {
            // (43.12) Preenche o fundo com branco puro (a impressora térmica precisa de fundo branco)
            drawColor(Color.WHITE)
            // (43.13) Desenha o texto centrado horizontalmente e ajustado verticalmente pela métrica
            drawText(text, (width + 20) / 2f, height - metrics.bottom + 5, paint)
        }

        // (43.14) Retorna o bitmap pronto com o texto desenhado para ser enviado à impressora
        return bitmap
    }
}
