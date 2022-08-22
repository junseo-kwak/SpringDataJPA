package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepositry;
    @Autowired
    private TeamRepository teamRepository;

    @PersistenceContext
    private EntityManager em;

    @Test
    void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepositry.save(member);
        Member findMember = memberRepositry.findById(savedMember.getId()).get();

        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(savedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(savedMember).isEqualTo(findMember);
    }

    @Test
    void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberRepositry.save(member1);
        memberRepositry.save(member2);

        // 단건 조회 체크
        Member findMember1 = memberRepositry.findById(member1.getId()).get();
        Member findMember2 = memberRepositry.findById(member2.getId()).get();

        assertThat(member1).isEqualTo(findMember1);
        assertThat(member2).isEqualTo(findMember2);

        // 전체 조회 체크
        List<Member> members = memberRepositry.findAll();
        assertThat(members.size()).isEqualTo(2);

        // 카운트 체크
        long count = memberRepositry.count();
        assertThat(count).isEqualTo(2);

        // 삭제 체크
        memberRepositry.delete(member1);
        memberRepositry.delete(member2);

        count = memberRepositry.count();

        assertThat(count).isEqualTo(0);
    }



    @Test
    void findByUsernameAndAgeGreaterThan(){
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("AAA",20);

        memberRepositry.save(member1);
        memberRepositry.save(member2);


        List<Member> members = memberRepositry.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);

    }

    @Test
    void testUser(){
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("AAA",20);

        memberRepositry.save(member1);
        memberRepositry.save(member2);

        List<Member> members = memberRepositry.findUser("AAA",10);
        assertThat(members.get(0)).isEqualTo(member1);

    }

    @Test
    void findMemberDto(){
        Team team = new Team("teamA");
        teamRepository.save(team);


        Member member1 = new Member("AAA",10);
        member1.setTeam(team);
        memberRepositry.save(member1);

        List<MemberDto> memberDto = memberRepositry.findMemberDto();
        for (MemberDto dto : memberDto) {
            System.out.println("dto = " + dto);
        }
    }

    @Test
    void paging(){
        memberRepositry.save(new Member("member1",10));
        memberRepositry.save(new Member("member2",10));
        memberRepositry.save(new Member("member3",10));
        memberRepositry.save(new Member("member4",10));
        memberRepositry.save(new Member("member5",10));
        int age = 10;
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"username"));
        Page<Member> page = memberRepositry.findByAge(age, pageRequest);

        // 엔티티는 외부에 노출하면 안되므로 DTO로 반드시 변환 후 리턴!
        Page<MemberDto> map = page.map(m -> new MemberDto(m.getId(), m.getUsername(), null));

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();


        assertThat(content.size()).isEqualTo(3);
        assertThat(totalElements).isEqualTo(5);
        assertThat(page.isFirst()).isTrue();
        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.hasNext()).isTrue();
        assertThat(page.getNumber()).isEqualTo(0);

    }

    @Test
    void BulkUpdate(){

        memberRepositry.save(new Member("member1",10));
        memberRepositry.save(new Member("member2",20));
        memberRepositry.save(new Member("member3",30));
        memberRepositry.save(new Member("member4",40));
        memberRepositry.save(new Member("member5",50));

        int count = memberRepositry.bulkAgePlus(30);

        List<Member> members = memberRepositry.findUser("member4", 41);
        for (Member member : members) {
            System.out.println("member = " + member);
        }
        assertThat(count).isEqualTo(3);
    }

    @Test
    void findMemberLazy(){

        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("member1",10,teamA);
        Member member2 = new Member("member2",10,teamB);

        memberRepositry.save(member1);
        memberRepositry.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepositry.findEntityGraphByUsername("member1");

        for (Member member : members) {
            System.out.println("member = " + member);
            System.out.println("member.teamClass = " + member.getTeam().getClass());
            System.out.println("member.getTeam().getName() = " + member.getTeam().getName());
        }

    }

    @Test
    void queryHint(){
        Member member = new Member("member1",10);

        memberRepositry.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepositry.findReadOnlyByUsername("member1");
        findMember.setUsername("member2");
        em.flush();
    }

    @Test
    void lock(){
        Member member = new Member("member1",10);

        memberRepositry.save(member);
        em.flush();
        em.clear();

        Member findMember = memberRepositry.findLockByUsername("member1");
    }

    @Test
    void callCustom(){
        Member member = new Member("member1",10);
        memberRepositry.save(member);
        List<Member> memberCustom = memberRepositry.findMemberCustom();

    }


}