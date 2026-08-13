document.addEventListener("DOMContentLoaded", function () {

    // 경력 사항
    const addCareerButton = document.getElementById("add-career");

    const careerContainer = document.getElementById("career-container");

    // 경력 추가
    if (careerContainer && addCareerButton){
        let careerIndex = careerContainer.querySelectorAll(".career-item").length;

        addCareerButton.addEventListener("click", function (){
            const careerItem = document.createElement("div");

            careerItem.classList.add("career-item");

            careerItem.innerHTML = `
                <input type="hidden" name="careers[${careerIndex}].id">     
                
                <div class="form-group">
                    <label>회사명</label>
                    <input type="text" name="careers[${careerIndex}].careerName">
                </div>     
                
                <div class="form-group">
                    <label>입사일</label>
                    <input type="date" name="careers[${careerIndex}].startDate">
                </div>      
                
                <div class="form-group">
                    <label>퇴사일</label>
                    <input type="date" name="careers[${careerIndex}].endDate">
                </div>
                
                <div class="form-group">
                    <label>담당 업무</label>
                    <textarea name="careers[${careerIndex}].duties" rows="5"></textarea>
                </div>
                
                <button type="button" class="remove-career">
                    경력 삭제
                </button>
            `;

            careerContainer.appendChild(careerItem);

            careerIndex++;

        })
    }

        // 경력 사항 삭제
        if (careerContainer){
                careerContainer.addEventListener("click", function(event){

                    if (event.target.classList.contains("remove-career")){

                        const careerItem = event.target.closest(".career-item");

                        if (careerItem){
                            // 화면에서 경력 삭제
                            careerItem.remove();

                            // 삭제 후 인덱스 다시 정리
                            reindexCareers();
                        }
                    }
                }
            );
        }

    // 경력 인덱스 다시 정리
    function reindexCareers() {

            const careerItems = document.querySelectorAll("#career-container .career-item");

            careerItems.forEach(function (item, index) {

                const fields =
                    item.querySelectorAll("input, textarea");

                fields.forEach(function (field) {

                    const name =
                        field.getAttribute("name");

                    if (name) {

                        field.setAttribute(
                            "name",
                            name.replace(
                                /careers\[\d+\]/,
                                `careers[${index}]`
                            )
                        );
                    }
                });
            });
        }

    // 포트폴리오
    const portfolioContainer = document.getElementById( "portfolio-container" );

    const addPortfolioButton = document.getElementById( "add-portfolio" );

    // 기존 포트폴리오 URL / FILE 변경
    if (portfolioContainer){
        portfolioContainer.addEventListener("change", function (event){
            if (!event.target.classList.contains( "portfolio-type" )){
                return;
            }

            const select = event.target;

            const portfolioItem = select.closest( ".portfolio-item" );

            const urlArea = portfolioItem.querySelector( ".portfolio-url" );

            const fileArea = portfolioItem.querySelector( ".portfolio-file" );

            // 둘 다 숨기기
            if (urlArea) {
                urlArea.style.display = "none";
            }

            if (fileArea) {
                fileArea.style.display = "none";
            }

            // URL 선택
            if (select.value === "URL") {
                if (urlArea) {
                    urlArea.style.display = "block";
                }
            }

            // FILE 선택
            else if (select.value === "FILE") {
                if (fileArea) {
                    fileArea.style.display = "block";
                }
            }
        });
    }

    // 포트폴리오 추가
    if ( portfolioContainer && addPortfolioButton ) {
        let portfolioIndex = portfolioContainer.querySelectorAll( ".portfolio-item" ).length;

        addPortfolioButton.addEventListener("click", function (){

            const portfolioItem = document.createElement("div");
            portfolioItem.classList.add( "portfolio-item" );

            portfolioItem.innerHTML = `
                <div class="form-group"> 
                    <label> 포트폴리오 제목 </label> 
                    <input type="text" name="portfolios[${portfolioIndex}].title"> 
                </div>
                
                <div class="form-group"> 
                    <label> 포트폴리오 유형 </label> 
                    <select class="portfolio-type" name="portfolios[${portfolioIndex}].type" required> 
                        <option value=""> 선택하세요 </option> 
                        <option value="URL"> URL </option> 
                        <option value="FILE"> 파일 </option> 
                    </select> 
                </div>
                
                <!-- URL --> 
                <div class="form-group portfolio-url" style="display:none;"> 
                    <label> 포트폴리오 URL </label> 
                    <input type="text" name="portfolios[${portfolioIndex}].url"> 
                </div>
                
                <!-- 첨부파일 --> 
                <div class="form-group portfolio-file" style="display:none;"> 
                    <label> 첨부파일 </label> 
                    <input type="file" name="portfolios[${portfolioIndex}].file"> 
                </div>
                
                <button type="button" class="remove-portfolio"> 포트폴리오 삭제 </button>
            `;
            portfolioContainer.appendChild( portfolioItem );
            portfolioIndex++;
        });
    }

    // 포트폴리오 삭제
    if (portfolioContainer){
        portfolioContainer.addEventListener("click", function (event){
            if (event.target.classList.contains("remove-portfolio")) {
                const portfolioItem = event.target.closest( ".portfolio-item" );

                if (portfolioItem){
                    // 화면에서 삭제
                    portfolioItem.remove();

                    // 인덱스 다시 정리
                    reindexPortfolios();
                }
            }
        });
    }

    // 포트폴리오 인덱스 정리
    function reindexPortfolios() {

        const portfolioItems = document.querySelectorAll( "#portfolio-container .portfolio-item" );

        portfolioItems.forEach( function (item, index) {

            const fields = item.querySelectorAll( "input, select" );

            fields.forEach( function (field) {

                const name = field.getAttribute( "name" );

                if (name) {
                    field.setAttribute( "name", name.replace( /portfolios\[\d+\]/, `portfolios[${index}]`)
                    );
                }
            });
        });
    }
});