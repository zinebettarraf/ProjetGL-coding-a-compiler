package fr.ensimag.deca.tree;

import fr.ensimag.deca.context.Type;
import fr.ensimag.deca.DecacCompiler;
import fr.ensimag.deca.context.ClassDefinition;
import fr.ensimag.deca.context.ContextualError;
import fr.ensimag.deca.context.EnvironmentExp;
import fr.ensimag.deca.tools.IndentPrintStream;
import fr.ensimag.ima.pseudocode.DVal;
import fr.ensimag.ima.pseudocode.GPRegister;
import fr.ensimag.ima.pseudocode.instructions.LOAD;


import java.io.PrintStream;
import org.apache.commons.lang.Validate;

/**
 * @author gl23
 * @date 01/01/2022
 */
public class Initialization extends AbstractInitialization {

    public AbstractExpr getExpression() {
        return expression;
    }

    private AbstractExpr expression;

    public void setExpression(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    public Initialization(AbstractExpr expression) {
        Validate.notNull(expression);
        this.expression = expression;
    }

    @Override
    protected void verifyInitialization(DecacCompiler compiler, Type t,
            EnvironmentExp localEnv, ClassDefinition currentClass)
            throws ContextualError {
        expression = expression.verifyRValue(compiler, localEnv, currentClass, t);
        
    }


    @Override
    public void decompile(IndentPrintStream s) {
        //throw new UnsupportedOperationException("Not yet implemented");
        s.print("= ");
        this.expression.decompile(s);
    }

    @Override
    protected
    void iterChildren(TreeFunction f) {
        expression.iter(f);
    }

    @Override
    protected void prettyPrintChildren(PrintStream s, String prefix) {
        expression.prettyPrint(s, prefix, true);
    }

    @Override
    protected void codeGenInit(DecacCompiler compiler){
        
        if(expression.getReg() == null){
            expression.setReg(compiler.getRegManager().getFreeRegister());;
        }
        
        expression.codeGenInst(compiler);
        DVal dval = expression.getDval();
        if(!(dval instanceof GPRegister)){
            compiler.addInstruction(new LOAD(expression.getDval(), expression.getReg()));
        }
        else{
            expression.setReg((GPRegister) dval);
        }
        
    }
    
    protected void codeGenARMInit(DecacCompiler compiler) {
        expression.codeGenARMInst(compiler);
    }
}
