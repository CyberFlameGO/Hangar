declare module 'hangar-api' {
    import { Model, Named } from 'hangar-api';
    import { NamedPermission, RoleCategory } from '~/types/enums';

    interface Role {
        assignable: boolean;
        rank?: number | null;
        value: string;
        roleId: number;
        category: RoleCategory;
        permissions: string;
        title: string;
        color: string;
    }

    interface User extends Model, Named {
        tagline: string | null;
        joinDate: string;
        roles: Role[];
        projectCount: number;
        isOrganization: boolean;
        locked: boolean;
    }

    interface ApiKey extends Model, Named {
        token?: string;
        tokenIdentifier?: string;
        permissions: NamedPermission[];
    }
}
