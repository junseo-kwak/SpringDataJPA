package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepositry;

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



}