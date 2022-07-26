Every member in the clan has a role. Role defines permissions and importance of person.
To get plugin working, it is required to have at least 2 roles: **Owner** and **Default**

Default role is given to all members by default.
Owner role is given to the clan creator.

Every role has its name, display name, weight and a list of **clan permissions**

Name is a simple identifier to use in commands.
Display name is.. i think you already understand.

## Weight
Weight basically means the priority of role.
For example, member having role with lower weight wouldnt be able to kick member with higher weight.

## Clan Permissions

You can setup permissions for every role. For example, default members wouldnt be able to invite members or create homes.
There's a simple table of all available clan permissions:

| Name              | Value                            |
|-------------------|----------------------------------|
| invite            | Invite members                   |
| kick              | Kick members                     |
| set_role          | Set members role                 |
| add_home          | Create homes                     |
| remove_home       | Remove homes                     |
| edit_others_homes | Edit/Delete other members homes  |
| set_display_name  | Rename the clan                  |
| disband           | Disband the clan                 |
| send_war_request  | Send war requests to other clans |
| accept_war        | Accept war requests              |

You can add as many roles as you want, but keep in mind that **Owner should always have the highest weight and Default the smallest one.**
To give all permissions to the role, simply use '*' like in example.
Use config.yml to setup roles.
That's how it looks like:

        roles:
        owner-role:
            name: 'owner'
            display_name: '<red>Owner'
            weight: 10
            permissions:
            - '*'

        default-role:
            name: 'default'
            display_name: '<gray>Member'
            weight: 1
            permissions: []

        other-roles:
            - name: 'moderator'
            display_name: '<aqua>Moderator'
            weight: 8
            permissions:
                - 'set_role'
                - 'send_war_request'
                - 'accept_war'
                - 'add_home'
                - 'remove_home'
                - 'invite'
                - 'kick'
                - 'set_display_name'


