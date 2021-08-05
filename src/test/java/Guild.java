import java.util.List;


public class Guild {
    private List<Member> members;
    public void addMember(Member member) {
        members.add(member);
        if(member.getGuild() != null)
            throw new IllegalStateException("member is in another guild.");
        member.setGuild(this);
    }
    public boolean isMember(Member member) {
        return members.contains(member);
    }
}

class Member {
    private transient Guild guild;
    public void setGuild(Guild guild) {
        this.guild = guild;
    }
    public Guild getGuild() {
        return guild;
    }
}


class Application {
    public static void main(String[] main) {
        Member member = new Member();
        Guild guild = new Guild();
        guild.addMember(member);

    }
}










