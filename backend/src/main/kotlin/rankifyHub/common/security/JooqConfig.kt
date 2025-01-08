package rankifyHub.common.security

import org.jooq.SQLDialect
import org.jooq.conf.RenderNameCase
import org.jooq.conf.RenderQuotedNames
import org.jooq.conf.Settings
import org.jooq.impl.DefaultConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class JooqConfig {
    @Bean
    fun settings(): Settings {
        return Settings()
            .withRenderQuotedNames(RenderQuotedNames.NEVER)
            .withRenderNameCase(RenderNameCase.LOWER)
    }

    @Bean
    fun configuration(
        @Autowired dataSource: DataSource,
        settings: Settings
    ): org.jooq.Configuration {
        return DefaultConfiguration()
            .set(dataSource)
            .set(SQLDialect.POSTGRES)
            .set(settings)
    }
}
