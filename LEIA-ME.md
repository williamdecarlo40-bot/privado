# App Doramas Drama Flix (WebView)

O app abre o site dentro dele mesmo: sem barra de cima e sem abrir no navegador.
Atualizar o site atualiza o app. Não precisa gerar o APK de novo.

## Como gerar o APK (uma vez)
1. Instale o **Android Studio** (grátis): developer.android.com/studio
2. Extraia este zip. Em Android Studio: **File → Open** e escolha a pasta `DoramasDramaFlix`.
3. Espere o "Gradle sync" terminar (barra de progresso embaixo; na primeira vez baixa arquivos e demora uns minutos). Se ele pedir para instalar o Android SDK ou atualizar o Gradle, aceite.
4. Menu **Build → Select Build Variant** (ou a aba "Build Variants" na lateral) e deixe **release** no módulo `app`.
5. Menu **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
6. Quando terminar, clique em "locate" na mensagem. O arquivo é `app/build/outputs/apk/release/app-release.apk`.

## Instalar no celular
- **Desinstale antes o app antigo** (o do PWABuilder).
- Mande o `app-release.apk` para o celular, toque nele e permita "instalar apps desconhecidos".
- Se o Play Protect avisar, toque em "Instalar mesmo assim".

## O que o app faz
- Abre `https://doramasdramaflix.com.br/` dentro do app, sem barra.
- Bloqueia gravação e captura de tela (isso também impede você de tirar print dentro do app).
- Vídeo em tela cheia, botão voltar, tela "Sem conexão" quando não há internet.
- Links de WhatsApp, e-mail e outros sites abrem fora do app.

## Mudar o endereço, o nome ou o ícone
- Endereço: `MainActivity.kt`, linha `START_URL`.
- Nome: `app/src/main/res/values/strings.xml`.
- Ícone: arquivos `ic_launcher.png` nas pastas `res/mipmap-*`.

## Limitação
O Google costuma **bloquear o login com Google** dentro de WebView (erro "disallowed_useragent"). O login só com e-mail funciona normalmente.
