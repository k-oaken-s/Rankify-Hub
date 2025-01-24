import { Category as CategoryType } from "@/types/Category";

import UserCategoryTile from "./UserCategoryTile";

interface UserCategoryListProps {
  categories: CategoryType[];
}

const UserCategoryList = ({ categories }: UserCategoryListProps) => (
  <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
    {categories.map((category) => (
      <UserCategoryTile key={category.id} category={category} />
    ))}
  </div>
);

export default UserCategoryList;
