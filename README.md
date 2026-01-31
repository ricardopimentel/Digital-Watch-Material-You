# Digital Widget Material You

Um widget moderno e altamente personalizável para Android, inspirado no design Material You. Este projeto oferece widgets elegantes que se adaptam ao seu estilo, exibindo informações essenciais como hora, data, bateria, próximo alarme e previsão do tempo.

## 📱 Recursos

- **Design Material You:** Cores dinâmicas que se harmonizam com o tema do seu dispositivo (Android 12+).
- **Altamente Personalizável:**
  - **Formatos do Fundo:** Orgânico, Arredondado, Quadrado, Circular.
  - **Estilos do Fundo:** Padrão, Transparente, Vidro (Glassmorphism), Preto/Branco translúcido.
  - **Cores do Texto/Ícones:** Padrão (adaptável), Branco, Preto, Vermelho, Verde, Azul.
- **Informações no Widget:**
  - Relógio Digital Material (Hora e Data).
  - Nível da Bateria (com ícone e porcentagem).
  - Próximo Alarme configurado no sistema.
  - Previsão do Tempo (Temperatura atual e Ícone de condição).
- **Layout Responsivo:** O widget se ajusta automaticamente a diferentes tamanhos na tela inicial.
- **Integração com AdMob:**
  - Anúncio de Abertura (App Open Ad).
  - Banner na tela de configuração.
  - Anúncio Nativo na tela de Menu/Informações.

## 🛠️ Configuração e Permissões

Para que o widget funcione corretamente com todas as funcionalidades (especialmente a previsão do tempo), o aplicativo requer a seguinte permissão:

- **Localização (ACCESS_COARSE_LOCATION):** Necessária para obter a temperatura local. O app solicitará esta permissão automaticamente ao ser aberto pela primeira vez.

## 🚀 Como Usar

1.  **Instale o App:** Compile e instale o APK no seu dispositivo.
2.  **Abra o App:** Conceda a permissão de localização quando solicitado para ativar a previsão do tempo.
3.  **Adicione o Widget:**
    - Vá para a tela inicial do Android.
    - Pressione e segure em um espaço vazio.
    - Selecione "Widgets".
    - Encontre "Digital Widget" e arraste para a tela.
4.  **Configure:** Ao adicionar (ou clicar no ícone do app), a tela de configuração abrirá. Escolha suas preferências de estilo, cor e formato.
5.  **Salve:** Clique em "Salvar e Adicionar Widget".

## 📂 Estrutura do Projeto

- **Activity Principal (`Info.java`):** Tela inicial com informações e solicitação de permissão.
- **Configuração (`WidgetConfigActivity.java`):** Tela para personalizar a aparência do widget.
- **Widget Provider (`Widget.java`):** Lógica principal de atualização e renderização do widget.
- **Helper (`WeatherHelper.java`):** Utilitário para buscar dados de clima (API Open-Meteo).

## 📝 Licença

Este projeto é de código aberto. Sinta-se à vontade para contribuir ou modificar para uso pessoal.

---

_Desenvolvido com foco em estética e funcionalidade._
