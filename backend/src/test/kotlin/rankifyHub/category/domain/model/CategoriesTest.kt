package rankifyHub.category.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import rankifyHub.shared.vo.SortOrder

class CategoriesTest :
  StringSpec({
    "filterByName は大文字小文字を区別せずにカテゴリー名で検索できる" {
      val category1 = mockk<Category> { every { name } returns "Pokemon" }
      val category2 = mockk<Category> { every { name } returns "Final Fantasy" }
      val categories = Categories.from(listOf(category1, category2))

      categories.filterByName("POK").toList() shouldHaveSize 1
      categories.filterByName("pok").toList() shouldHaveSize 1
      categories.filterByName("fantasy").toList() shouldHaveSize 1
    }

    "sortByReleaseDate は指定された順序でカテゴリーをソートできる" {
      val date1 = LocalDate.of(2024, 1, 1)
      val date2 = LocalDate.of(2024, 2, 1)

      val category1 = mockk<Category> { every { releaseDate } returns date1 }
      val category2 = mockk<Category> { every { releaseDate } returns date2 }
      val categories = Categories.from(listOf(category1, category2))

      categories.sortByReleaseDate(SortOrder.ASC).toList().map { it.releaseDate } shouldBe
        listOf(date1, date2)

      categories.sortByReleaseDate(SortOrder.DESC).toList().map { it.releaseDate } shouldBe
        listOf(date2, date1)
    }

    "検索とソートを組み合わせることができる" {
      val date1 = LocalDate.of(2024, 1, 1)
      val date2 = LocalDate.of(2024, 2, 1)

      val category1 =
        mockk<Category> {
          every { name } returns "Pokemon"
          every { releaseDate } returns date1
        }
      val category2 =
        mockk<Category> {
          every { name } returns "Final Fantasy"
          every { releaseDate } returns date2
        }

      val categories = Categories.from(listOf(category1, category2))

      val result = categories.filterByName("Pokemon").sortByReleaseDate(SortOrder.DESC).toList()

      result shouldHaveSize 1
      result.first().name shouldBe "Pokemon"
    }
  })
