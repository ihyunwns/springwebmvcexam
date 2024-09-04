let passID = false;
let passPWC = false;
let passAge = false;
let passNick = false;
let passPW = false;

const form = document.getElementById('floatingInput');
const specialCharPattern = /[^a-zA-Z0-9]/g;

form.addEventListener('input', validateID);

function validateID() {
    const checkBtn = document.getElementById('checkID');
    if (specialCharPattern.test(form.value)) {
        form.value = form.value.replace(specialCharPattern, '');
        alert("영어와 숫자만 입력 가능합니다.");
    }
    checkBtn.disabled = form.value.length <= 4;
}

const check = document.getElementById('checkID');
check.addEventListener('click', function (event) {
    const userID = document.getElementById('floatingInput').value;
    // const idLabel = document.querySelector(`label[for='${document.getElementById('floatingInput').id}']`);
    fetch(`/checkID?userID=${encodeURIComponent(userID)}`)
        .then(response => response.json())
        .then(response => {
            if (response.Available) {
                alert("사용 가능한 아이디입니다.");
                const idBox = document.getElementById('floatingInput');
                check.classList.add('checked');
                check.disabled = true;

                idBox.readOnly = true;
                // 자동완성 시, 배경색이 덮어져서 색깔이 바뀌지 않는 걸 시간차를 둬서 자동완성 스타일을 덮어쓰도록 함
                setTimeout(() => {
                    idBox.style.backgroundColor = 'darkgray'; // 강제로 배경색 설정
                    idBox.style.boxShadow = '0 0 0px 1000px darkgray inset'; // 배경색 덮어쓰기를 방지
                }, 0);
                passID = true;
            } else {
                alert("이미 존재하는 아이디입니다.");
            }
        })
})

const nick_form = document.getElementById('floatingNickname');
const nicknameCharPattern = /[ ~!@#$%";'^,&*()_+|</>=>`?:{[\}]/g;
const completeKorean = /[가-힣]/g;
const incompleteKorean = /[ㄱ-ㅎㅏ-ㅣ]/g;

nick_form.addEventListener('keyup', function (event) {
    const value = nick_form.value;

    if (nicknameCharPattern.test(value)) {
        nick_form.value = nick_form.value.replace(nicknameCharPattern, '');
        alert('특수문자는 입력을 할 수 없습니다.');
    }

    if (value.length < 2 || value.length > 8 || incompleteKorean.test(value)) {
        nick_form.classList.remove('pass');
        passNick = false;
    } else if (value.length > 1 && value.length < 9 || completeKorean.test(value)){
        passNick = true;
        nick_form.classList.add('pass');
    }
});

nick_form.addEventListener('focusout', function (event) {
    const value = nick_form.value;
    if (value.length < 2 || value.length > 8 || incompleteKorean.test(value)) {
        nick_form.classList.remove('pass');
        passNick = false;
    } else if (value.length > 1 && value.length < 9 || completeKorean.test(value)){
        nick_form.classList.add('pass');
        passNick = true;
    }
});


const password_form = document.getElementById('floatingPassword');
const passwordDigitChar = /\d/g;
const passwordSpecialChar = /[!@#$%^~&*]/g;

const inavailablePassword = /[^\d!@#$%^~&*a-zA-Z]/g

password_form.addEventListener('input', (event) => {
    //const lastChar = event.target.value[event.target.value.length-1];
    if (event.inputType === 'deleteContentBackward' || event.inputType === 'deleteContentForward') {
        return;
    }

    const value = event.target.value;
    if (passwordConfirm.value.length > 0) {
        passwordConfirm.value = "";
        passPWC = false;
        passwordConfirm.classList.remove('pass');
    }
    const special_count = (value.match(passwordSpecialChar) || []).length;
    const digit_count = (value.match(passwordDigitChar) || []).length;

    if (inavailablePassword.test(password_form.value)) {
        password_form.value = password_form.value.replace(inavailablePassword, "");
        alert("올바른 문자를 입력해주세요 \n가능한 특수문자: !, @, #, $, ~, %, ^, &, *");
    }

    if (value.length < 8 || value.length > 16 || special_count < 1 || digit_count < 2) {
        password_form.classList.remove("pass");
        passPW = false;
    } else if (value.length > 7 && value.length < 17 && special_count >= 1 && digit_count >= 2) {
        password_form.classList.add("pass");
        passPW = true;
    }
});

password_form.addEventListener('focusout', (event) => {
    const value = event.target.value;
    if (value === "") {
        return;
    }
    const special_count = (value.match(passwordSpecialChar) || []).length;
    const digit_count = (value.match(passwordDigitChar) || []).length;

    // 문자열에서 패턴에 일치하는 모든 값을 배열로 반환, 일치하는 값이 없으면 null을 반환하므로, || []를 사용하여 빈 배열로 처리
    if (value.length < 8 || value.length > 16 || special_count < 1 || digit_count < 2) {
        password_form.classList.remove("pass");
        passPW = false;
    } else if (value.length > 7 && value.length < 17 && special_count >= 1 && digit_count >= 2) {
        passPW = true;
        password_form.classList.add("pass");
    }

});


const passwordConfirm = document.getElementById('floatingPasswordConfirm');
passwordConfirm.addEventListener('focusout', (event)=>{
    const value = event.target.value;
    if (value === "") {
        return;
    }
    const target = document.getElementById('floatingPassword').value;
    if (value !== target) {
        passPWC = false;
        passwordConfirm.classList.remove('pass');
    } else {
        passPWC = true;
        passwordConfirm.classList.add('pass');
    }
})

const inputAge = document.getElementById('floatingAge');
inputAge.addEventListener('change', (event) => {
    const value = event.target.value;
    if (value > 0 && value < 130) {
        passAge = true;
        inputAge.classList.add('pass');
    } else {
        passAge = false;
        inputAge.classList.remove('pass');
    }
});


const signupForm = document.getElementById('signupForm');
    signupForm.addEventListener('submit', (event) => {
        if (!passID || !passNick || !passPW || !passPWC || !passAge) {
            event.preventDefault();
            alert('다시 한번 입력을 확인해주세요!');
        } else {
            alert('회원가입이 완료되었습니다!');
        }
});