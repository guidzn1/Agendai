# Agendai

Um app Android para gerenciamento de espaços e eventos, desenvolvido como projeto da disciplina de Programação para Dispositivos Móveis.

---

## 📝 Descrição

O **Agendai** foi pensado para organizar agendamentos de quadras e espaços esportivos de forma simples e intuitiva. Permite:

- Criar, editar e excluir eventos;
- Gerenciar dinamicamente quais espaços estão disponíveis (Configurações);
- Validar automaticamente conflitos de horário e local;
- Visualizar horários livres e disponíveis por espaço;
- Gerar relatórios de faturamento por dia ou mês;
- Acompanhar o histórico de eventos e marcar como concluídos.

---

## 🚀 Funcionalidades Principais

- **Tela Principal**  
  – Calendário interativo  
  – Lista de eventos agendados  
  – Speed Dial para Adicionar evento e Buscar por nome  
  – Drawer (menu lateral) para acessar Configurações, Horários Livres, Faturamento e Histórico  

- **Agenda de Eventos**  
  – Título, contato, data, horário de início/término, seleção de espaço e condição de pagamento (50%/100%)  
  – Validação de conflitos de horário/local  

- **Configurações**  
  – CRUD de espaços (nome, valor por hora)  
  – Ícones de editar e excluir para cada espaço  

- **Horários Livres / Disponíveis**  
  – Escolha uma data e visualize, por espaço, quais horários ainda não estão ocupados

- **Faturamento**  
  – Filtrar por **Dia** ou **Mês**  
  – Cálculo do total faturado (considerando valor por hora e duração dos eventos)  
  – Identificação da “Área mais usada” (a que acumulou mais horas de uso)

- **Histórico**  
  – Lista completa de eventos passados, cancelados ou concluídos  

---

## 🛠 Tecnologias

- **Linguagem**: Java  
- **Plataforma**: Android (SDK mínimo 21+)  
- **UI**: Material Components, RecyclerView, CoordinatorLayout, AppBarLayout, SpeedDialView  
- **Persistência**: DAO com armazenamento em JSON interno (SharedPreferences ou arquivo local)  
- **Navegação**: Navigation Drawer, Intents (Activities)  
- **Internacionalização**: Suporte a formatos de data e idioma do dispositivo  

---

## 📥 Como Instalar

1. **Clone** este repositório  
   ```bash
   git clone https://github.com/guidzn1/Agendai
   ```
2. **Abra** o Android Studio e selecione _Open an existing project_ → pasta do repositório.  
3. Aguarde o **Gradle Sync**.  
4. Conecte um **dispositivo** ou inicie um **emulador** (Android 5.0+).  
5. Clique em **Run** (▶️) para instalar e executar o app.

---

## 🎯 Uso Rápido

1. Na **tela inicial**, selecione uma data no calendário.  
2. Toque no **botão flutuante** (Speed Dial) para adicionar um evento ou buscar por título.  
3. No **menu lateral**, configure novos espaços ou acesse relatórios e histórico.  
4. Para gerar faturamento, escolha **Dia** ou **Mês**, selecione a data e toque em **GERAR RELATÓRIO**.  
5. Visualize seu faturamento total e a área mais utilizada.

---

## 🤝 Contribuição

1. Faça um **fork** deste repositório.  
2. Crie uma **branch** para sua feature (`git checkout -b feature/nome-da-feature`).  
3. **Commit** suas mudanças (`git commit -m 'Adiciona feature X'`).  
4. **Push** para a branch (`git push origin feature/nome-da-feature`).  
5. Abra um **Pull Request** explicando suas alterações.

---

## 📄 License

Licenciado sob a [MIT License](LICENSE).

---

## 📬 Contato

- **LinkedIn**: [Guilherme Lima](https://www.linkedin.com/in/guilherme-l-938351246/)  
- **E‑mail**: gui09lima@gmail.com
