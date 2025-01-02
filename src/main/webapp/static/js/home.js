window.onload = function() {
    var errorDiv = document.getElementById('errorDiv');
    if (errorDiv) {
        setTimeout(function() {
            errorDiv.classList.add('slide-out');
            setTimeout(function () {
                errorDiv.remove();
            },500);
        }, 3000);  // 3초 후에 내려가도록 설정
    }
}