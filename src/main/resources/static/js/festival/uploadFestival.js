console.log("festival JS IN");
console.log(apiKey);

async function syncFestivals() {
    try {
        const listUrl = `https://apis.data.go.kr/B551011/KorService2/searchFestival2?numOfRows=50&MobileOS=WEB&MobileApp=CHUCK&_type=json&arrange=R&eventStartDate=20260101&serviceKey=${apiKey}`;
        const listRes = await fetch(listUrl);
        const listDatas = await listRes.json();

        const festivalList = listDatas.response?.body?.items?.item || [];

        const festivalPromises = festivalList.map(async (item) => {

            const contentId = item.contentid || item.contentId;

            const commonUrl = `https://apis.data.go.kr/B551011/KorService2/detailCommon2?MobileOS=WEB&MobileApp=CHUCK&_type=json&contentId=${contentId}&serviceKey=${apiKey}`;
            const introUrl = `https://apis.data.go.kr/B551011/KorService2/detailIntro2?MobileOS=WEB&MobileApp=CHUCK&_type=json&contentId=${contentId}&contentTypeId=15&serviceKey=${apiKey}`;
            const infoUrl = `https://apis.data.go.kr/B551011/KorService2/detailInfo2?MobileOS=WEB&MobileApp=CHUCK&_type=json&contentId=${contentId}&contentTypeId=15&serviceKey=${apiKey}`;

            const [commonRes, introRes, infoRes] = await Promise.all([
                fetch(commonUrl).then(res => res.json()),
                fetch(introUrl).then(res => res.json()),
                fetch(infoUrl).then(res => res.json())
            ]);

            const commonItems = commonRes.response?.body?.items?.item;
            const introItems = introRes.response?.body?.items?.item;
            const infoItems = infoRes.response?.body?.items?.item;

            const common = (Array.isArray(commonItems) ? commonItems[0] : commonItems) || {};
            const feeInfo = (Array.isArray(introItems) ? introItems[0] : introItems) || {};
            const overview = (Array.isArray(infoItems) ? infoItems[0] : infoItems) || {};

            return {
                contentId: String(contentId),
                title: common.title || '',
                homepage: common.homepage || null,
                mapX: common.mapx ? parseFloat(common.mapx) : null,
                mapY: common.mapy ? parseFloat(common.mapy) : null,
                overview: common.overview || null,
                tel: common.tel || null,
                telName: common.telname || null,
                ageLimit: feeInfo.agelimit || null,
                eventPlace: feeInfo.eventplace || null,
                zipCode: common.zipcode || null,
                playTime: feeInfo.playtime || null,
                program: feeInfo.program || null,
                useTime: feeInfo.usetimefestival || null,
                startDate: feeInfo.eventstartdate || feeInfo.eventestartdate || null,
                endDate: feeInfo.eventenddate || null,
                firstImage: common.firstimage || null,
                secondImage: common.secondimage || null
            };

        });

        const festivals = await Promise.all(festivalPromises);

        await saveFestivalToDB(festivals);

    } catch (error) {
        console.error('동기화 중 오류 발생:', error);
    }
}

/* DB에 데이터 저장 */
async function saveFestivalToDB(festivals) {

    try {

        const url = "/api/festival/saveAll";
        const config = {
            method: "POST",
            headers: {
                "Content-Type" : "application/json; charset=UTF-8"
            },
            body: JSON.stringify(festivals)
        }

        const response = await fetch(url, config);
        const datas = await response.text();
        return datas;

    } catch (error) {
        console.log(error)
    }

}

// window.addEventListener("DOMContentLoaded", () => {
//     console.log("축제 동기화 시작 ...");
//     syncFestivals();
// });