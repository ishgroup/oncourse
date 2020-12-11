package ish.oncourse.server.api.v1.function

import ish.oncourse.server.api.dao.EntityRelationDao
import ish.oncourse.server.api.v1.model.SaleDTO
import ish.oncourse.server.api.v1.model.SaleTypeDTO
import ish.oncourse.server.api.validation.EntityValidator
import ish.oncourse.server.cayenne.ArticleProduct
import ish.oncourse.server.cayenne.Course
import ish.oncourse.server.cayenne.EntityRelation
import ish.oncourse.server.cayenne.EntityRelationType
import ish.oncourse.server.cayenne.MembershipProduct
import ish.oncourse.server.cayenne.Module
import ish.oncourse.server.cayenne.Product
import ish.oncourse.server.cayenne.Qualification
import ish.oncourse.server.cayenne.VoucherProduct
import ish.oncourse.server.cayenne.glue.CayenneDataObject
import ish.util.EntityUtil
import org.apache.cayenne.ObjectContext
import org.apache.cayenne.query.SelectById

import static ish.oncourse.server.api.function.CayenneFunctions.getRecordById
import static ish.oncourse.server.cayenne.EntityRelationType.DEFAULT_SYSTEM_TYPE_ID

class EntityRelationFunctions {

    static validateRelatedEntitiesBeforeSave(ObjectContext context, Long entityId, List<SaleDTO> relatedEntities) {
        relatedEntities.findAll { it.entityToId != null }.each { relatedProduct ->
            Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(relatedProduct.type.getCayenneClassName())
            CayenneDataObject entityTo = SelectById.query(clzz, relatedProduct.entityToId).selectOne(context)
            if (!entityTo) {
                EntityValidator.throwClientErrorException(entityId, 'relatedSellables', "$clzz.simpleName with id=$relatedProduct.entityToId not found.")
            }
        }

        relatedEntities.findAll { it.entityFromId != null }.each { relatedProduct ->
            Class<? extends CayenneDataObject> clzz = EntityUtil.entityClassForName(relatedProduct.type.getCayenneClassName())
            CayenneDataObject entityTo = SelectById.query(clzz, relatedProduct.entityFromId).selectOne(context)
            if (!entityTo) {
                EntityValidator.throwClientErrorException(entityId, 'relatedSellables', "$clzz.simpleName with id=$relatedProduct.entityFromId not found.")
            }
        }

        if (relatedEntities.any { it.entityToId == null && it.entityFromId == null }) {
            EntityValidator.throwClientErrorException(entityId, 'relatedSellables', "You should specify id of related entity.")
        }
    }

    static void updateRelatedEntities(ObjectContext context, Long entityId, String entityName, List<SaleDTO> relatedEntities) {
        validateRelatedEntitiesBeforeSave(context, entityId, relatedEntities)
        List<EntityRelation> currentRelations = []
        List<SaleDTO> relationsToSave

        currentRelations += EntityRelationDao.getRelatedFrom(context, entityName, entityId)
        currentRelations += EntityRelationDao.getRelatedTo(context, entityName, entityId)

        //remove relations that missed from resulted list
        context.deleteObjects(currentRelations.findAll { !(it.id in relatedEntities.findAll { it.id != null }*.id) })

        relatedEntities.each { it ->
            EntityRelation relation
            if (it.id == null) {
                relation = context.newObject(EntityRelation)
            } else {
                relation = getRecordById(context, EntityRelation, it.id)
            }
            if (it.entityToId != null) {
                relation.toEntityAngelId = it.entityToId
                relation.toEntityIdentifier = it.type.getCayenneClassName()
                relation.fromEntityAngelId = entityId
                relation.fromEntityIdentifier = entityName
            } else if (it.entityFromId != null) {
                relation.toEntityAngelId = entityId
                relation.toEntityIdentifier = entityName
                relation.fromEntityAngelId = it.entityFromId
                relation.fromEntityIdentifier = it.type.getCayenneClassName()
            } else {
                throw new IllegalArgumentException("A related entity should be specified.")
            }
            relation.relationType = getRecordById(context, EntityRelationType, it.relationId ?: DEFAULT_SYSTEM_TYPE_ID)
        }

        context.commitChanges()
    }

    static SaleDTO toRestToEntityRelation(EntityRelation relation) {
        SaleDTO dto
        if (Course.simpleName == relation.toEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Course, relation.toEntityAngelId).selectOne(relation.context))
        } else if (Product.simpleName == relation.toEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Product, relation.toEntityAngelId).selectOne(relation.context))
        } else if (Module.simpleName == relation.toEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Module, relation.toEntityAngelId).selectOne(relation.context))
        } else if (Qualification.simpleName == relation.toEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Qualification, relation.toEntityAngelId).selectOne(relation.context))
        } else {
            throw new IllegalArgumentException("Unsupported entity type relation")
        }
        dto.id = relation.id
        dto.entityToId = relation.toEntityAngelId
        dto.relationId = relation.relationType?.id
        dto
    }


    static SaleDTO toRestFromEntityRelation(EntityRelation relation) {
        SaleDTO dto
        if (Course.simpleName == relation.fromEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Course, relation.fromEntityAngelId).selectOne(relation.context))
        } else if (Product.simpleName == relation.fromEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Product, relation.fromEntityAngelId).selectOne(relation.context))
        } else if (Module.simpleName == relation.fromEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Module, relation.fromEntityAngelId).selectOne(relation.context))
        } else if (Qualification.simpleName == relation.fromEntityIdentifier) {
            dto = toRestSalable(SelectById.query(Qualification, relation.fromEntityAngelId).selectOne(relation.context))
        } else {
            throw new IllegalArgumentException("Unsupported entity type relation")
        }
        dto.id = relation.id
        dto.entityFromId = relation.fromEntityAngelId
        dto.relationId = relation.relationType?.id
        dto
    }


    static SaleDTO toRestSalable(Course course) {
        new SaleDTO().with { s ->
            s.active =  course.currentlyOffered || course.isShownOnWeb
            s.code = course.code
            s.name = course.with { "$it.name $it.code" }
            s.type = SaleTypeDTO.COURSE
            s
        }
    }

    static SaleDTO toRestSalable(Product product) {
        new SaleDTO().with { s ->
            s.name = product.name
            s.code = product.sku
            s.active = Boolean.TRUE
            switch (product) {
                case VoucherProduct:
                    s.type = SaleTypeDTO.VOUCHER
                    break
                case MembershipProduct:
                    s.type = SaleTypeDTO.MEMBERSHIP
                    break
                case ArticleProduct:
                    s.type = SaleTypeDTO.PRODUCT
                    break
                default:
                    throw new IllegalArgumentException("${product.class}")
            }
            s
        }
    }

    static SaleDTO toRestSalable(Module module) {
        new SaleDTO().with { s ->
            s.name = module.title
            s.code = module.nationalCode
            s.active = module.isOffered
            s.type = SaleTypeDTO.MODULE
            s
        }
    }

    static SaleDTO toRestSalable(Qualification qualification) {
        new SaleDTO().with { s ->
            s.name = qualification.title
            s.code = qualification.nationalCode
            s.active = qualification.isOffered
            s.type = SaleTypeDTO.QUALIFICATION
            s
        }
    }
}
