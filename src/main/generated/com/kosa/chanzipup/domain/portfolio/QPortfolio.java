package com.kosa.chanzipup.domain.portfolio;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPortfolio is a Querydsl query type for Portfolio
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPortfolio extends EntityPathBase<Portfolio> {

    private static final long serialVersionUID = 2100520043L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPortfolio portfolio = new QPortfolio("portfolio");

    public final com.kosa.chanzipup.domain.QBaseEntity _super = new com.kosa.chanzipup.domain.QBaseEntity(this);

    public final com.kosa.chanzipup.domain.account.QAccount account;

    public final com.kosa.chanzipup.domain.buildingtype.QBuildingType buildingType;

    public final ListPath<PortfolioConstructionType, QPortfolioConstructionType> constructionTypes = this.<PortfolioConstructionType, QPortfolioConstructionType>createList("constructionTypes", PortfolioConstructionType.class, QPortfolioConstructionType.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdDateTime = _super.createdDateTime;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Integer> floor = createNumber("floor", Integer.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastUpdatedDateTime = _super.lastUpdatedDateTime;

    public final ListPath<PortfolioImage, QPortfolioImage> portfolioImages = this.<PortfolioImage, QPortfolioImage>createList("portfolioImages", PortfolioImage.class, QPortfolioImage.class, PathInits.DIRECT2);

    public final NumberPath<Integer> projectBudget = createNumber("projectBudget", Integer.class);

    public final StringPath projectLocation = createString("projectLocation");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final StringPath title = createString("title");

    public QPortfolio(String variable) {
        this(Portfolio.class, forVariable(variable), INITS);
    }

    public QPortfolio(Path<? extends Portfolio> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPortfolio(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPortfolio(PathMetadata metadata, PathInits inits) {
        this(Portfolio.class, metadata, inits);
    }

    public QPortfolio(Class<? extends Portfolio> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.account = inits.isInitialized("account") ? new com.kosa.chanzipup.domain.account.QAccount(forProperty("account"), inits.get("account")) : null;
        this.buildingType = inits.isInitialized("buildingType") ? new com.kosa.chanzipup.domain.buildingtype.QBuildingType(forProperty("buildingType")) : null;
    }

}

