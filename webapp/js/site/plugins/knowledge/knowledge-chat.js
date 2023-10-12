/**
 * Represents a Knowledge Chat instance.
 */
class KnowledgeChat {
    /**
     * Creates a new KnowledgeChat instance.
     */
    constructor() {
        /**
         * The markdown renderer.
         * @type {markdownit}
         */
        this.md = window.markdownit({
            html: true,
            linkify: true,
            typographer: true,
            highlight: function (str, lang) {
                if (lang && hljs.getLanguage(lang)) {
                  try {
                    let highlightedCode = hljs.highlight(str, { language: lang, ignoreIllegals: true }).value;
                    return `<pre class="hljs bg-black p-5 rounded-5"><code class="position-relative">${highlightedCode}</code></pre>`;
                  } catch (__) {}
                }
            
                return '';
              }
        });

        /**
         * The chat container element.
         * @type {HTMLElement}
         */
        this.chatContainer = document.body;

        /**
         * The chat messages container element.
         * @type {HTMLElement}
         */
        this.chatMessages = null;

        /**
         * The event source for streaming content.
         * @type {EventSource}
         */
        this.eventSource = null;

        /**
         * The current assistant card element.
         * @type {HTMLElement}
         */
        this.currentAssistantCard = null;

        /**
         * Whether the chat is currently fetching content.
         * @type {boolean}
         */
        this.isFetching = false;

        this.sessionId = null;


        // Set up markdown renderer rules
        this.md.renderer.rules.table_open = function (tokens, idx) {
            return '<table class="table table-bordered rounded-table">';
        };

    }

    /**
     * Handles a new chat message.
     * @param {Object} data - The message data.
     */
    handleMessage(data) {
        if (data.message && data.message.trim() !== '') {
            if (!this.currentAssistantCard) {
                this.currentAssistantCard = this.addChatMessage('Assistant', data.message);
            } else {
                this.currentAssistantCard.querySelector('.card-body').innerHTML = this.md.render(data.message);
                this.currentAssistantCard.querySelector('.card-message').classList.remove('bg-blink');
                this.scrollToBottom();
            }
        }
    }

    /**
     * Adds a new chat message to the chat messages container.
     * @param {string} type - The message type (either 'Assistant' or 'User').
     * @param {string} text - The message text.
     * @returns {HTMLElement} The message box element.
     */
    addChatMessage(type, text) {
        const messageBox = document.createElement('div');
        const typeClass = (type === 'Assistant') ? 'card-message-assistant text-break bg-light px-5 py-4 rounded-3 fs-5 mb-3' : 'card-message-user text-break fs-4';
        const alignmentClass = (type === 'Assistant') ? 'justify-content-start' : 'justify-content-end';
        messageBox.className = `${alignmentClass} mb-3`;
        let innerHTML = `<div>`;
        console.log(text);
        innerHTML += `
            <div class="card-message mt-2 border-bottom ${typeClass} ${text.trim() === '' ? 'bg-blink' : ''}">
                <div class="card-body">`;
        if (type === "Assistant") {
            innerHTML += `
            <p class="card-text placeholder-glow placeholder-wave">
            <span class="placeholder col-7"></span>
            <span class="placeholder col-4"></span>
            <span class="placeholder col-4"></span>
            <span class="placeholder col-6"></span>
            <span class="placeholder col-8"></span>
          </p>
            `;
        }
        innerHTML += `
                    <p class="card-text p-0">${this.md.render(text)}</p>
                </div>
            </div>
        </div>`;
        messageBox.innerHTML = innerHTML;
        this.chatMessages.appendChild(messageBox);
        this.scrollToBottom();
        return messageBox;
    }

    /**
     * Fetches streamed content from the server.
     * @param {string} question - The user's question.
     * @param {string} botId - The project ID.
     * @param {string} pipelineId - The pipeline ID.
     */
    fetchStreamedContent(question, projectId) {
        if (this.isFetching) return;
    
        this.isFetching = true;
        const data = JSON.stringify({
            action: "startWorkflow",
            question: question,
            botId: projectId,
            botSessionId: this.sessionId
        });
    
        
        fetch('../../rest/knowledge/api/v1/bots/chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: data,
        })
        .then(response => response.json())
        .then(data => {
            const sessionAnswerId = data.sessionId;
            console.log('sessionAnswerId:', sessionAnswerId);
            if( this.sessionId === null ) {
                this.sessionId = sessionAnswerId;
            }
            if (this.eventSource) {
                this.eventSource.close();
            }
            this.eventSource = new EventSource(`../../rest/knowledge/api/v1/bots/chat/sse?sessionId=${sessionAnswerId}`);
            this.eventSource.addEventListener('pipeline', (event) => {
                const json = JSON.parse(event.data);
                for (const key in json) {
                    if (json.hasOwnProperty(key)) {
                        const element = json[key];
                        if (element.container === "chat") {
                            this.handleMessage(element);
                        }
                    }
                }
            });
            this.eventSource.addEventListener('message', (event) => {
                if (event.data === "CLOSE") {
                    this.eventSource.close();
                    console.log('Event Source closed');
                }
            });
            this.eventSource.onerror = (error) => {
                console.error("Error:", error);
                this.eventSource.close();
            };
        })
        .catch(error => {
            console.error("Error:", error);
        })
        .finally(() => {
            this.isFetching = false;
        });
    
        this.addChatMessage('User', question);
        this.currentAssistantCard = null;
    }
    

    /**
     * Scrolls the chat messages container to the bottom.
     */
    scrollToBottom() {
        setTimeout(() => {
            const main = document.getElementById("lutece-main");
            main.lastElementChild.scrollIntoView();
        }, 0);
    }
    /**
     * Initializes the KnowledgeChat instance.
     * @param {HTMLElement} chatMessages - The chat messages container element.
     * @param {HTMLElement} inputField - The input field element.
     * @param {string} botId - The project ID.
     * @param {string} sessionId - The session ID.
     * @param {HTMLElement} pipelineSelect - The pipeline select element.
     */
    initialize(chatMessages, inputField, botId, sessionId) {
        this.sessionId = sessionId;
        this.chatMessages = chatMessages;

         // Apply markdown to all existing .card-message elements
    const messageElements = chatMessages.querySelectorAll('.card-message .card-body');
    messageElements.forEach(el => {
        let messageText = el.textContent.replace(/\\n/g, '\n');
        messageText = messageText.replace(/\\u([\d\w]{4})/gi, (match, grp) => {
            return String.fromCharCode(parseInt(grp, 16));
        });
        el.innerHTML = messageText;
    });

    const messageAssistantElements = chatMessages.querySelectorAll('.card-message-assistant .card-body');
    messageAssistantElements.forEach(el => {
        let messageText = el.textContent.replace(/\\n/g, '\n');
        messageText = messageText.replace(/\\u([\d\w]{4})/gi, (match, grp) => {
            return String.fromCharCode(parseInt(grp, 16));
        });
        el.innerHTML = this.md.render(messageText);
    });

    inputField.addEventListener('keydown', (e) => {
            if (e.key === "Enter") {
                e.preventDefault();
                if (!this.isFetching && inputField.value.trim() !== '') {
                    this.fetchStreamedContent(inputField.value, botId);
                    this.currentAssistantCard = this.addChatMessage('Assistant', '');
                    inputField.value = '';

                }
            }
        });
    }
}
