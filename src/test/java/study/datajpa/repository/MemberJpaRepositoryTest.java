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
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Test
    void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberJpaRepository.save(member);
        Member findMember = memberJpaRepository.find(savedMember.getId());

        assertThat(savedMember.getId()).isEqualTo(findMember.getId());
        assertThat(savedMember.getUsername()).isEqualTo(findMember.getUsername());
        assertThat(savedMember).isEqualTo(findMember);
    }

    @Test
    void basicCRUD(){
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        // 단건 조회 체크
        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        Assertions.assertThat(member1).isEqualTo(findMember1);
        Assertions.assertThat(member2).isEqualTo(findMember2);

        // 전체 조회 체크
        List<Member> members = memberJpaRepository.findAll();
        Assertions.assertThat(members.size()).isEqualTo(2);

        // 카운트 체크
        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);

        // 삭제 체크
        memberJpaRepository.delete(member1);
        memberJpaRepository.delete(member2);

        count = memberJpaRepository.count();

        assertThat(count).isEqualTo(0);
    }

    @Test
    void findByUsernameAndAgeGreaterThan(){
        Member member1 = new Member("AAA",10);
        Member member2 = new Member("AAA",20);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);


        List<Member> members = memberJpaRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(members.get(0).getUsername()).isEqualTo("AAA");
        assertThat(members.get(0).getAge()).isEqualTo(20);

    }

    @Test
    void paging(){
        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",10));
        memberJpaRepository.save(new Member("member3",10));
        memberJpaRepository.save(new Member("member4",10));
        memberJpaRepository.save(new Member("member5",10));

        int age = 10;
        int offset = 0;
        int limit = 3;

        List<Member> members = memberJpaRepository.findByPage(age, offset, limit);
        long totalCount = memberJpaRepository.totalCount(age);

        assertThat(members.size()).isEqualTo(3);
        assertThat(totalCount).isEqualTo(5);
    }

    @Test
    void BulkUpdate(){

        memberJpaRepository.save(new Member("member1",10));
        memberJpaRepository.save(new Member("member2",20));
        memberJpaRepository.save(new Member("member3",30));
        memberJpaRepository.save(new Member("member4",40));
        memberJpaRepository.save(new Member("member5",50));

        int count = memberJpaRepository.bulkAgePlus(30);

        assertThat(count).isEqualTo(3);
    }



}