package com.example.chook.dev;

import com.example.chook.member.entity.BusinessRegistration;
import com.example.chook.member.entity.EmployerProfile;
import com.example.chook.member.entity.JobSeekerProfile;
import com.example.chook.member.entity.Member;
import com.example.chook.member.entity.enums.Gender;
import com.example.chook.member.entity.enums.MemberRole;
import com.example.chook.member.entity.enums.MemberStatus;
import com.example.chook.member.repository.BusinessRegistrationRepository;
import com.example.chook.member.repository.EmployerProfileRepository;
import com.example.chook.member.repository.JobSeekerProfileRepository;
import com.example.chook.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class DevMemberDataInitializer {

  private final MemberRepository memberRepository;
  private final JobSeekerProfileRepository jobSeekerProfileRepository;
  private final EmployerProfileRepository employerProfileRepository;
  private final BusinessRegistrationRepository businessRegistrationRepository;
  private final PasswordEncoder passwordEncoder;

  private final Random random = new Random();

  private static final long DUMMY_PROFILE_RANGE_START = 1L;

  // id,name,username,phone,email - 지정된 180명 실사례형 더미 프로필 (RECRUITER 50 + JOB_SEEKER 100 + JOB_EQUIP 30)
  private static final String DUMMY_PROFILE_CSV = """
    1,윤서혁,dctop_02,01050165258,dctop_025258@naver.com
    2,한광환,ftcjj1997,01019026831,ftcjj19976831@naver.com
    3,박선린,wjlve229,01066171396,wjlve2291396@naver.com
    4,유현영,kbxosf394,01022525832,kbxosf3945832@naver.com
    5,하희서,derp395,01098076526,derp3956526@naver.com
    6,전광율,ywgn51kr,01085131999,ywgn51kr1999@gmail.com
    7,노서민,ggoctv1988,01056997673,ggoctv19887673@gmail.com
    8,허석이,fipff70,01000352457,fipff702457@gmail.com
    9,황정성,cllm_0660,01078115114,cllm_06605114@naver.com
    10,허규현,zywe489,01010397267,zywe4897267@gmail.com
    11,임건린,potb_4950,01058008495,potb_49508495@naver.com
    12,민윤성,fsgkrh1999,01099022988,fsgkrh19992988@gmail.com
    13,전수이,royc2003,01052672624,royc20032624@naver.com
    14,강광혁,idze540,01008958511,idze5408511@gmail.com
    15,양지일,rjowz_1989,01088073988,rjowz_19893988@naver.com
    16,송수현,wfvl882z,01032073568,wfvl882z3568@gmail.com
    17,이하원,apiulz_35,01027095776,apiulz_355776@gmail.com
    18,백도환,shik2001,01096963502,shik20013502@gmail.com
    19,우순결,lfmxuec6609z,01062102480,lfmxuec6609z2480@gmail.com
    20,주건한,gratucl86q,01002848695,gratucl86q8695@gmail.com
    21,민용은,tmggq4247z,01023512670,tmggq4247z2670@gmail.com
    22,고찬솔,fkxp0854kr,01023885778,fkxp0854kr5778@gmail.com
    23,권가은,krus995q,01056048123,krus995q8123@gmail.com
    24,유다한,gcaozjq1999,01011652671,gcaozjq19992671@naver.com
    25,한예현,oxjyfp2636,01087505606,oxjyfp26365606@naver.com
    26,조희현,qvsltz697q,01047288082,qvsltz697q8082@gmail.com
    27,주혜환,wefuiws551,01024524740,wefuiws5514740@gmail.com
    28,권성희,mnltis1990,01015712479,mnltis19902479@gmail.com
    29,조건린,eanhzo_9671,01002439281,eanhzo_96719281@naver.com
    30,배희연,dohzd672,01054489366,dohzd6729366@naver.com
    31,신우원,goekr227,01074697669,goekr2277669@gmail.com
    32,윤찬영,vbehto_443,01042545876,vbehto_4435876@naver.com
    33,강선울,otenlh4894,01043114464,otenlh48944464@naver.com
    34,남윤결,duwvaw2004,01044021284,duwvaw20041284@naver.com
    35,구태율,oczx2002,01027444411,oczx20024411@naver.com
    36,정가성,vhhpzm1990,01055431652,vhhpzm19901652@gmail.com
    37,박채담,zuqdzdz884q,01001890726,zuqdzdz884q0726@gmail.com
    38,강다강,qxmoqg9974x,01077112030,qxmoqg9974x2030@gmail.com
    39,양광아,ouecwqg_1422,01008961435,ouecwqg_14221435@gmail.com
    40,안영솔,wcjjhsa_3087,01099789728,wcjjhsa_30879728@naver.com
    41,서진진,ilpt93,01042783521,ilpt933521@gmail.com
    42,전다민,dzws983q,01005466589,dzws983q6589@naver.com
    43,권정우,qqmsq1989,01091253396,qqmsq19893396@gmail.com
    44,우은성,qyhrflg065,01093311846,qyhrflg0651846@naver.com
    45,최다희,btffw_775,01029957181,btffw_7757181@gmail.com
    46,황철결,gdkcb751,01059078549,gdkcb7518549@naver.com
    47,심성은,slxiddq5395,01046078677,slxiddq53958677@naver.com
    48,백동영,vksiig64x,01080078555,vksiig64x8555@gmail.com
    49,윤훈일,zpjp14x,01035122457,zpjp14x2457@naver.com
    50,구가우,siiawo_079,01005341339,siiawo_0791339@naver.com
    51,최은율,hrml253,01016553290,hrml2533290@naver.com
    52,홍우진,iatejs2003,01000985574,iatejs20035574@naver.com
    53,배서결,vxcvgpn_872,01070651739,vxcvgpn_8721739@naver.com
    54,황시아,edtby1998,01069393446,edtby19983446@gmail.com
    55,우현일,majhqz65,01045784863,majhqz654863@gmail.com
    56,하은호,vvzc1244z,01039376063,vvzc1244z6063@gmail.com
    57,조규한,srxwip2000,01063593378,srxwip20003378@naver.com
    58,백용규,iyitez08,01017880371,iyitez080371@gmail.com
    59,임수빈,bjszptu_53,01084839942,bjszptu_539942@gmail.com
    60,하석성,udgqw_9751,01001458098,udgqw_97518098@gmail.com
    61,이훈호,gzjayv3109q,01061740028,gzjayv3109q0028@gmail.com
    62,윤연희,bkul1997,01005214624,bkul19974624@naver.com
    63,곽경연,wbkjtjb76,01077305817,wbkjtjb765817@gmail.com
    64,심재진,afuxiik4062,01080627444,afuxiik40627444@gmail.com
    65,고재별,ynuscp53x,01032471447,ynuscp53x1447@gmail.com
    66,민광혁,rknt91,01067007335,rknt917335@naver.com
    67,허경규,onkq90z,01043522492,onkq90z2492@gmail.com
    68,서하진,eszvzzr0588,01079675121,eszvzzr05885121@gmail.com
    69,이경산,uopcizs1990,01064638761,uopcizs19908761@naver.com
    70,백용호,heyfy8826,01051355191,heyfy88265191@gmail.com
    71,주지우,yugf4646,01029486200,yugf46466200@gmail.com
    72,조정솔,pistwz_75,01012141098,pistwz_751098@gmail.com
    73,황수결,tmnmi1994,01092304864,tmnmi19944864@naver.com
    74,조광진,tjkofo1997,01029619373,tjkofo19979373@gmail.com
    75,임건랑,bdim181kr,01019723857,bdim181kr3857@gmail.com
    76,심서규,zpus2004,01052045773,zpus20045773@naver.com
    77,박호민,lkkf_7987,01001236531,lkkf_79876531@gmail.com
    78,성소원,yhhyz764q,01039721639,yhhyz764q1639@gmail.com
    79,임연윤,jlgm1997,01061652656,jlgm19972656@naver.com
    80,조훈환,rdzz49q,01041936905,rdzz49q6905@naver.com
    81,오훈빈,uetwhah1929,01054984111,uetwhah19294111@gmail.com
    82,안재준,otewxv1995,01032227937,otewxv19957937@naver.com
    83,안경희,mery7669,01032944937,mery76694937@gmail.com
    84,성우희,njxcvxf1351,01025058454,njxcvxf13518454@naver.com
    85,차영솔,gafblso90,01090477964,gafblso907964@gmail.com
    86,허혜한,qjenn9879,01047432411,qjenn98792411@naver.com
    87,박훈혜,fcaps20,01045771929,fcaps201929@gmail.com
    88,주순결,wznkho_8964,01049967341,wznkho_89647341@gmail.com
    89,오찬담,hsuxo_512,01026980712,hsuxo_5120712@naver.com
    90,정건별,fxhxfet_9426,01029573532,fxhxfet_94263532@gmail.com
    91,남시담,jkofd1989,01013384662,jkofd19894662@gmail.com
    92,노영율,doexz1982,01096748270,doexz19828270@gmail.com
    93,양현혜,ankasnh8689x,01095262370,ankasnh8689x2370@gmail.com
    94,남호희,rwqwtc_346,01070863998,rwqwtc_3463998@naver.com
    95,권우울,bbyxoa021q,01092558653,bbyxoa021q8653@naver.com
    96,강용일,lvit3702,01094394660,lvit37024660@gmail.com
    97,황도서,juekj1991,01071045510,juekj19915510@naver.com
    98,구순결,hkcx51q,01012211227,hkcx51q1227@gmail.com
    99,안태별,chyiqui_486,01053664133,chyiqui_4864133@gmail.com
    100,주태결,vkcopo7079z,01042687446,vkcopo7079z7446@naver.com
    101,안은은,lppn4097x,01012616566,lppn4097x6566@gmail.com
    102,백시준,ftorfj755,01030149191,ftorfj7559191@gmail.com
    103,고영훈,mlsihv03x,01061921159,mlsihv03x1159@naver.com
    104,배재빈,apfpksf8018z,01031164933,apfpksf8018z4933@gmail.com
    105,우하일,qvshpcd1991,01065413166,qvshpcd19913166@naver.com
    106,차수별,evqwy1996,01064474157,evqwy19964157@naver.com
    107,전지혁,elttoc_25,01095774247,elttoc_254247@naver.com
    108,홍지이,wcwdvf1985,01067885551,wcwdvf19855551@gmail.com
    109,전호혁,zuxzw71,01013283166,zuxzw713166@gmail.com
    110,허도서,swlmpw0966x,01073984325,swlmpw0966x4325@naver.com
    111,주희환,culd40,01092616192,culd406192@gmail.com
    112,안윤규,khsg_0020,01055403186,khsg_00203186@gmail.com
    113,정선한,cjhj66,01005613279,cjhj663279@gmail.com
    114,신윤현,pjzi_26,01026729088,pjzi_269088@naver.com
    115,곽순슬,hbxymcm_0803,01033845149,hbxymcm_08035149@naver.com
    116,이철연,fxykhs446,01051009913,fxykhs4469913@naver.com
    117,홍광희,cllpi6101,01004358237,cllpi61018237@gmail.com
    118,윤서환,ngzqmn45kr,01017861708,ngzqmn45kr1708@naver.com
    119,황지담,xeyausr2003,01038516025,xeyausr20036025@naver.com
    120,배도솔,ioafoh2004,01019720423,ioafoh20040423@naver.com
    121,고민한,fzky1987,01085739001,fzky19879001@gmail.com
    122,정철울,rdhwykv320,01027511445,rdhwykv3201445@naver.com
    123,허예솔,cuhs1987,01093028198,cuhs19878198@naver.com
    124,조영현,avzd_366,01052800941,avzd_3660941@gmail.com
    125,윤지솔,ylsmj121,01028062245,ylsmj1212245@naver.com
    126,강가한,fxnquuz_3549,01053132870,fxnquuz_35492870@gmail.com
    127,곽소별,crgvx_79,01059399123,crgvx_799123@gmail.com
    128,우서일,vvzoe_02,01025092179,vvzoe_022179@gmail.com
    129,최서아,wwyj_3131,01019796196,wwyj_31316196@gmail.com
    130,권철빈,theaso0699z,01061430336,theaso0699z0336@naver.com
    131,고동준,gbgca1993,01043514866,gbgca19934866@naver.com
    132,허도윤,iybe1982,01039874664,iybe19824664@naver.com
    133,조다훈,sssghgb_5400,01008640269,sssghgb_54000269@gmail.com
    134,윤예호,xgqkjje24z,01085160684,xgqkjje24z0684@naver.com
    135,차현한,oghkwao8661q,01019981494,oghkwao8661q1494@naver.com
    136,구진결,kkih907,01057115591,kkih9075591@naver.com
    137,주시린,gromekq1987,01004233738,gromekq19873738@naver.com
    138,권준솔,hkqefv4530z,01007475518,hkqefv4530z5518@naver.com
    139,최태윤,xshv3962q,01051853187,xshv3962q3187@naver.com
    140,전서한,wufff24,01007709660,wufff249660@gmail.com
    141,구예혁,wlfhnqp56kr,01055264323,wlfhnqp56kr4323@gmail.com
    142,안예경,xuupr_1042,01094759837,xuupr_10429837@gmail.com
    143,문용솔,hmtso_98,01062284835,hmtso_984835@gmail.com
    144,윤정호,xnwjhbw93,01098918103,xnwjhbw938103@naver.com
    145,유현준,fuoeuu66q,01044057987,fuoeuu66q7987@naver.com
    146,이민희,icnhbb_455,01046194719,icnhbb_4554719@gmail.com
    147,이선영,tpejq_36,01076501801,tpejq_361801@gmail.com
    148,손하산,sfrakcn2003,01062746789,sfrakcn20036789@gmail.com
    149,조연율,xrmgacc6105,01071229240,xrmgacc61059240@naver.com
    150,박연규,tctbwre2003,01008149238,tctbwre20039238@gmail.com
    151,한하진,cxmol3306,01089191402,cxmol33061402@gmail.com
    152,김석울,nrqlsm_6385,01090618880,nrqlsm_63858880@gmail.com
    153,유태환,nojv1994,01004397040,nojv19947040@naver.com
    154,강순혜,rlivv2002,01067172389,rlivv20022389@gmail.com
    155,차석규,xmvt161,01093477149,xmvt1617149@naver.com
    156,허영별,atolpyu_61,01081663846,atolpyu_613846@naver.com
    157,송성환,udpxnvo1986,01027691791,udpxnvo19861791@gmail.com
    158,윤소산,rxnwkac161z,01096107253,rxnwkac161z7253@gmail.com
    159,조규영,nussb399kr,01090728842,nussb399kr8842@gmail.com
    160,우찬랑,heree_1022,01018049095,heree_10229095@gmail.com
    161,노호산,uife1997,01050223327,uife19973327@gmail.com
    162,차민민,gqxgbxj1989,01005755426,gqxgbxj19895426@gmail.com
    163,하윤현,qfqf_4157,01097697143,qfqf_41577143@naver.com
    164,정현빈,ofgk1980,01066757091,ofgk19807091@gmail.com
    165,구윤린,krmndku2628kr,01090810535,krmndku2628kr0535@naver.com
    166,홍우일,xwdeqgc_7889,01094520074,xwdeqgc_78890074@gmail.com
    167,문준희,qxilqh1589,01013776236,qxilqh15896236@gmail.com
    168,심영이,mcqchv1980,01086857202,mcqchv19807202@gmail.com
    169,임경호,dncjag_6711,01055817907,dncjag_67117907@naver.com
    170,정진규,rnuk_806,01019627553,rnuk_8067553@gmail.com
    171,양철연,zfkviux1985,01025211686,zfkviux19851686@gmail.com
    172,고하담,fevaboe5811z,01054968789,fevaboe5811z8789@naver.com
    173,허순담,fkdpt6273q,01050310596,fkdpt6273q0596@naver.com
    174,구시빈,wntc1688q,01053052873,wntc1688q2873@naver.com
    175,백준원,xcpog77x,01053284145,xcpog77x4145@gmail.com
    176,성광울,zkfooe2002,01031812791,zkfooe20022791@gmail.com
    177,고규혜,kquvm4020,01003965759,kquvm40205759@naver.com
    178,노동준,funspr_201,01097335570,funspr_2015570@naver.com
    179,차연산,zhpfyo1996,01029898856,zhpfyo19968856@gmail.com
    180,주정희,rxwmgju1990,01043572237,rxwmgju19902237@gmail.com
    """;

  @Transactional
  public void generateSampleMembers(
    long recruiterCount,
    long jobSeekerCount,
    long jobEquipCount
  ) {

    log.info("테스트용 사용자 데이터 삽입 시작");

    if (memberRepository.count() == 0) {

      Set<Integer> generatedNumbers = new HashSet<>();

      for (long i = 0; i < recruiterCount + jobEquipCount; i++) {
        generatedNumbers.add(random.nextInt(100000));
      }

      Iterator<Integer> businessNumberGenerator = generatedNumbers.iterator();

      // RECRUITER
      for (long i = 1; i <= recruiterCount; i++) {
        Member member = addMember("r", "행사 구인자", MemberRole.RECRUITER, i);
        addEmployerProfile(member, i);
        addBusinessRegistration(member, businessNumberGenerator);

      }

      // JOB_SEEKER
      for (long i = 1; i <= jobSeekerCount; i++) {
        Member member = addMember("s", "일반 구직자", MemberRole.JOB_SEEKER, i);
        addIndividualProfile(member);
      }

      // JOB_EQUIP
      for (long i = 1; i <= jobEquipCount; i++) {
        Member member = addMember("e", "전문 구직자", MemberRole.JOB_EQUIP, i);
        addIndividualProfile(member);
        addBusinessRegistration(member, businessNumberGenerator);
      }

    }

    // username이 "admin"인 ADMIN 계정 추가
    // 이미 존재해도 삭제 후 다시 생성
    memberRepository.deleteByUsername("admin");
    memberRepository.flush();
    memberRepository.save(
      Member.builder()
        .username("admin")
        .passwordHash(passwordEncoder.encode("admin"))
        .name("관리자")
        .phone("01000000000")
        .phoneVerified(true)
        .email("admin@example.com")
        .role(MemberRole.ADMIN)
        .status(MemberStatus.ACTIVE)
        .build()
    );

    log.info("테스트용 사용자 데이터 삽입 완료");

  }

  // id 1~180(RECRUITER 50 + JOB_SEEKER 100 + JOB_EQUIP 30, addMember로 생성된 범위)만 대상으로
  // name/username/phone/email을 실사례형으로 덮어씀. ADMIN 및 그 이후 생성되는 실제 가입 계정은
  // 전부 id가 180보다 크므로 이 범위 밖이라 영향받지 않음. id=1의 email이 아직 @example.com
  // placeholder면 미갱신 상태로 보고 진행하고, 아니면(이미 갱신됨) 그대로 건너뜀
  @Transactional
  public void updateMemberDummyProfiles() {

    boolean alreadyUpdated = memberRepository.findById(DUMMY_PROFILE_RANGE_START)
      .map(m -> !m.getEmail().endsWith("@example.com"))
      .orElse(true);
    if (alreadyUpdated) return;

    log.info("더미 회원 프로필(이름/아이디/전화번호/이메일) 실사례형으로 갱신 시작");

    for (String line : DUMMY_PROFILE_CSV.strip().split("\\R")) {
      String[] cols = line.strip().split(",");
      long id = Long.parseLong(cols[0]);
      String name = cols[1];
      String username = cols[2];
      String phone = cols[3];
      String email = cols[4];

      memberRepository.findById(id).ifPresent(member -> {
        if (member.getRole() == MemberRole.ADMIN) return;

        member.setName(name);
        member.setUsername(username);
        member.setPasswordHash(passwordEncoder.encode(username));
        member.setPhone(phone);
        member.setEmail(email);
        memberRepository.save(member);
      });
    }

    log.info("더미 회원 프로필 갱신 완료");
  }

  private Member addMember(String usernamePrefix,
                           String namePrefix,
                           MemberRole role,
                           long index) {
    String username = String.format("%s%02d", usernamePrefix, index);

    return memberRepository.save(
      Member.builder()
        .username(username)
        .passwordHash(passwordEncoder.encode(username))
        .name(String.format("%s #%02d", namePrefix, index))
        .phone("01000000000")
        .phoneVerified(true)
        .email(String.format("%s@example.com", username))
        .role(role)
        .status(MemberStatus.ACTIVE)
        .build()
    );
  }

  private void addEmployerProfile(Member member, long index) {
    employerProfileRepository.save(
      EmployerProfile.builder()
        .member(member)
        .companyName(String.format("행사 주최 기업 #%02d", index))
        .ceoName(String.format("행사 대표자 #%02d", index))
        .streetAddress("-")
        .foundedAt(LocalDate.of(2000, 1, 1))
        .build()
    );
  }

  private void addIndividualProfile(Member member) {

    Gender[] genders = Gender.values();
    YearMonth yearMonth = YearMonth.of(
      random.nextInt(1950, Year.now().getValue() - 16 + 1),
      random.nextInt(1, 12 + 1)
    );

    jobSeekerProfileRepository.save(
      JobSeekerProfile.builder()
        .member(member)
        .gender(genders[random.nextInt(genders.length)])
        .birthDate(LocalDate.of(
          yearMonth.getYear(),
          yearMonth.getMonth(),
          random.nextInt(1, yearMonth.lengthOfMonth() + 1)
        ))
        .build()
    );

  }

  private void addBusinessRegistration(Member member, Iterator<Integer> iterator) {

    businessRegistrationRepository.save(
      BusinessRegistration.builder()
        .member(member)
        .businessNumber(String.format("TEST-%05d", iterator.next()))
        .verified(true)
        .verifiedAt(LocalDateTime.of(2026, 1, 1, 0, 0, 0))
        .build()
    );

  }
}
