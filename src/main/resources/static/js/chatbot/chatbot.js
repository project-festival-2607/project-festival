console.log("chatbot.js 실행");

const chatBtn = document.getElementById("chatBtn");
const chatBox = document.getElementById("chatBox");
const closeBtn = document.getElementById("closeBtn");

// 챗봇 열기
chatBtn.addEventListener("click", () => {
    chatBox.classList.add("show");
});

// 챗봇 닫기
closeBtn.addEventListener("click", () => {
    chatBox.classList.remove("show");
});

const sendBtn = document.getElementById("sendBtn");
const message = document.getElementById("message");
const chatBody = document.querySelector(".chat-body");

// 전송 버튼 클릭
sendBtn.addEventListener("click", async () => {

    const question = message.value;

    if(question.trim() === ""){

        alert("메시지를 입력하세요");
        return;
    }

    // 사용자 질문 화면 출력
    chatBody.innerHTML += `
        <div class="my-question">${question}</div>
        `;

    // 입력창 비우기
    message.value = "";

    // 서버 전송
    const response = await fetch("/chatbot/ask", {
        method:"POST",
        headers:{
            "Content-Type":"application/json"
        },
        body:JSON.stringify({
            model:"llama-3.3-70b-versatile",
            messages:[
                {
                    role:"user",
                    content:question
                }
            ]
        })
    });
    // 서버 응답 받기
    const data = await response.json();

    console.log("서버 응답:", data);

    if (!response.ok) {
        console.error("서버 오류:", data);
        chatBody.innerHTML += `
        <p>
            🤖 잠시 후 다시 시도해주세요.
        </p>
    `;
        return;
    }
    if (!data.choices || !data.choices[0]) {
        console.error("AI 응답 형식 오류:", data);
        chatBody.innerHTML += `
        <p>
            🤖 AI 응답을 받아오지 못했습니다.
        </p>
    `;
        return;
    }
    let answer = data.choices[0].message.content;

    const formattedAnswer = answer.replace(/\n/g, '<br>');

    chatBody.innerHTML += `
        <div class="chat-hi">
                 ${formattedAnswer}
        </div>
`;
    chatBody.scrollTop = chatBody.scrollHeight;
});