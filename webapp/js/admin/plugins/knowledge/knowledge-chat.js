class KnowledgeChat {
    /**
     * Creates an instance of the KnowledgeChat class.
     */
    constructor() {
        this.md = window.markdownit({
            html: true,
            linkify: true,
            typographer: true,
        });

        this.chatContainer = document.getElementById('chat-scroll');

        this.md.renderer.rules.table_open = function (tokens, idx) {
            return '<table class="table table-bordered">';
        };
    }
    /**
     * Adds a chat message to the chatMessages element.
     * @param {string} type - The type of the message (e.g., 'Assistant' or 'User').
     * @param {string} text - The text content of the message.
     * @param {HTMLElement} chatMessages - The DOM element to append the message to.
     * @returns {HTMLElement} The created message box element.
     */
    addChatMessage(type, text, chatMessages) {
        const messageBox = document.createElement('div');
        const typeClass = (type === 'Assistant') ? 'card-message-assistant' : 'card-message-user fw-semibold text-primary-emphasis bg-primary-subtle border border-primary-subtle rounded-2 p-0';
        const alignmentClass = (type === 'Assistant') ? 'justify-content-start' : 'justify-content-end';

        messageBox.className = `d-flex ${alignmentClass} mb-3`;

        let innerHTML = `<div>`;

        if (type === "Assistant") {
            innerHTML += `<h2><i class="ti ti-robot"></i> ${type} <span class="badge border text-primary py-1 rounded-pill">GPT 3.5</span></h2>`;
        }

        innerHTML += `
            <div class="card card-message ${typeClass}">
                <div class="card-body">
                    <p class="card-text p-0">${this.md.render(text)}</p>
                </div>
            </div>
        </div>`;

        messageBox.innerHTML = innerHTML;
        chatMessages.appendChild(messageBox);
        this.scrollToBottom();

        return messageBox;
    }

    /**
     * Fetches streamed content from the server and adds it to the chatMessages element.
     * @param {string} question - The user's question.
     * @param {HTMLElement} chatMessages - The DOM element to append the message to.
     * @param {HTMLInputElement} inputField - The input field element.
     */
    fetchStreamedContent(question, modelName, chatMessages, inputField) {

        const temperature = document.getElementById('temperature').value || null;
        const topP = document.getElementById('topP').value || null;
        const maxTokens = document.getElementById('maxTokens').value || null;
        const presencePenalty = document.getElementById('presencePenalty').value || null;
        const frequencyPenalty = document.getElementById('frequencyPenalty').value || null;

    let queryString = `question=${encodeURIComponent(question)}&projectId=1&modelName=${encodeURIComponent(modelName)}`;
    if (temperature) queryString += `&temperature=${encodeURIComponent(temperature)}`;
    if (topP) queryString += `&topP=${encodeURIComponent(topP)}`;
    if (maxTokens) queryString += `&maxTokens=${encodeURIComponent(maxTokens)}`;
    if (presencePenalty) queryString += `&presencePenalty=${encodeURIComponent(presencePenalty)}`;
    if (frequencyPenalty) queryString += `&frequencyPenalty=${encodeURIComponent(frequencyPenalty)}`;

        this.addChatMessage('User', question, chatMessages);

        let fullText = '';
        let card;

        fetch(`http://localhost:8080/test/rest/knowledge/api/v1/projects/answer?${queryString}`)
            .then(response => {
                if (!response.ok || !response.body) {
                    throw new Error("Failed to get streamed content.");
                }
                const reader = response.body.getReader();

                const readStream = () => {
                    return reader.read().then(({
                        done,
                        value
                    }) => {
                        const textChunk = new TextDecoder().decode(value);
                        fullText += textChunk;

                        if (!card) {
                            card = this.addChatMessage('Assistant', '', chatMessages);
                        } else {
                            card.querySelector('.card-text').innerHTML = this.md.render(fullText);
                            this.scrollToBottom();
                        }

                        if (!done) {
                            return readStream();
                        }
                    });
                };
                return readStream();
            })
            .catch(error => {
                console.error("Error fetching streamed content:", error);
                this.addChatMessage('Assistant', 'Error fetching streamed content.', chatMessages);
            });
    }

    /**
     * Scrolls to the bottom of the chatMessages element.
     * @param {HTMLElement} chatMessages - The DOM element to scroll.
     */
    scrollToBottom() {
        this.chatContainer.scrollTop = this.chatContainer.scrollHeight;
    }

    /**
     * Initializes the chat, setting up event listeners for the inputField.
     * @param {HTMLElement} chatMessages - The DOM element to append messages to.
     * @param {HTMLInputElement} inputField - The input field element.
     */
    initialize(chatMessages, inputField, selectField) {
        inputField.addEventListener('keydown', (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                const modelName = selectField.options[selectField.selectedIndex].value;
                this.fetchStreamedContent(inputField.value, modelName, chatMessages, inputField);
                inputField.value = '';
            }
        });
    }
}