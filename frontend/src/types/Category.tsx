import { Item } from "./Item";

export interface Category {
  id: string;
  name: string;
  description: string;
  image: string;
  releaseDate: string;
  items: Item[];
  createdAt: string;
}
